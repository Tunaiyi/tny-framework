/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.common.digest.rsa;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;

import javax.crypto.BadPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RSA 工具契约（回归修复：分段加解密块大小原用 getOutputSize 推算，多块数据必抛
 * IllegalBlockSizeException；共享静态 KeyFactory 非线程安全改为逐次创建）。
 * 另按现状钉桩受控失败方向：非法密钥文本（解析上抛 / 重建返 null 两套入口）、密文结构与篡改、
 * 签名值篡改、多组密钥对交叉、容器未注册回查。
 */
class RSAUtilsTest {

    private static RSAKeyPair keyPair1024() throws Exception {
        return RSAUtils.getKeyPair();
    }

    @Test
    void defaultKeyPairIs1024Bit() throws Exception {
        RSAKeyPair pair = keyPair1024();
        assertEquals(1024, pair.getPublicKey().getModulus().bitLength());
        assertEquals(1024, pair.getPrivateKey().getModulus().bitLength());
        assertNotNull(pair.getKeyPair());
    }

    /** 密钥长度必须是 64 的倍数：非法值走受控异常（该分支原逻辑正确，钉死） */
    @Test
    void keySizeMustBeMultipleOf64() {
        assertThrows(IllegalArgumentException.class, () -> RSAUtils.getKeyPair(1000));
        assertDoesNotThrow(() -> RSAUtils.getKeyPair(2048));
    }

    /** Base64 字符串密钥互转：编码→解析→模数一致 */
    @Test
    void keyStringConversionRoundTrip() throws Exception {
        RSAKeyPair pair = keyPair1024();
        String pubWord = RSAUtils.key2Base64(pair.getPublicKey());
        String priWord = RSAUtils.key2Base64(pair.getPrivateKey());
        RSAPublicKey parsedPub = RSAUtils.toPublicKey(pubWord);
        RSAPrivateKey parsedPri = RSAUtils.toPrivateKey(priWord);
        assertEquals(pair.getPublicKey().getModulus(), parsedPub.getModulus());
        assertEquals(pair.getPrivateKey().getModulus(), parsedPri.getModulus());
        assertEquals(pair.getPublicKey().getPublicExponent(), parsedPub.getPublicExponent());
    }

    /** 公钥加密 → 私钥解密：单块（≤117B）与多块（300B → 3 段）都成立（回归原分段缺陷） */
    @Test
    void encryptDecryptMultiBlockRoundTrip() throws Exception {
        RSAKeyPair pair = keyPair1024();
        byte[] single = new byte[100];
        Arrays.fill(single, (byte) 0x5A);
        assertArrayEquals(single, RSAUtils.decrypt(RSAUtils.encrypt(single, pair.getPublicKey()),
                pair.getPrivateKey()));

        byte[] multi = new byte[300]; // 1024bit: 117/117/66 三段，密文应为 384 字节
        new Random().nextBytes(multi);
        byte[] encrypted = RSAUtils.encrypt(multi, pair.getPublicKey());
        assertEquals(384, encrypted.length, "多块密文长度应为 3×128");
        assertArrayEquals(multi, RSAUtils.decrypt(encrypted, pair.getPrivateKey()));

        // 字符串密钥入口：私钥加密 → 公钥解密（与 encrypt/decrypt 对称）
        byte[] privateCipher = RSAUtils.encryptByPrivateKey(single, RSAUtils.key2Base64(pair.getPrivateKey()));
        assertArrayEquals(single, RSAUtils.decryptByPublicKey(privateCipher, RSAUtils.key2Base64(pair.getPublicKey())));
        // 公钥加密 → 私钥解密（字符串入口）
        byte[] publicCipher = RSAUtils.encrypt(single, pair.getPublicKey());
        assertArrayEquals(single, RSAUtils.decryptByPrivateKey(publicCipher, RSAUtils.key2Base64(pair.getPrivateKey())));
    }

    /** 恰好 117 与 118 字节边界：117 单块、118 触发两段 */
    @Test
    void blockBoundary117And118() throws Exception {
        RSAKeyPair pair = keyPair1024();
        byte[] at117 = new byte[117];
        assertEquals(128, RSAUtils.encrypt(at117, pair.getPublicKey()).length);
        byte[] at118 = new byte[118];
        assertEquals(256, RSAUtils.encrypt(at118, pair.getPublicKey()).length);
        assertArrayEquals(at118, RSAUtils.decrypt(RSAUtils.encrypt(at118, pair.getPublicKey()),
                pair.getPrivateKey()));
    }

    @Test
    void signAndVerifyMd5AndSha1() throws Exception {
        RSAKeyPair pair = keyPair1024();
        String data = "订单号=T20260930001&金额=648";
        for (String algorithm : new String[]{RSAUtils.SIGN_MD5_ALGORITHM, RSAUtils.SIGN_SHA1_ALGORITHMS}) {
            String sign = RSAUtils.sign(data, pair.getPrivateKey(), algorithm);
            assertTrue(RSAUtils.verify(sign, data, pair.getPublicKey(), algorithm), algorithm + " 验签应通过");
            assertFalse(RSAUtils.verify(sign, data + "0", pair.getPublicKey(), algorithm),
                    algorithm + " 数据被篡改应验签失败");
        }
        // 字符串密钥 + 默认算法重载
        String sign = RSAUtils.sign(data, RSAUtils.key2Base64(pair.getPrivateKey()));
        assertTrue(RSAUtils.verify(sign, data, RSAUtils.key2Base64(pair.getPublicKey())));
    }

    /** 模/指数方式重建公钥（getPublicKey(String,String) 路径），重建密钥可参与验签 */
    @Test
    void keyRebuildFromModulusAndExponent() throws Exception {
        RSAKeyPair pair = keyPair1024();
        RSAPublicKey rebuilt = RSAUtils.getPublicKey(
                pair.getPublicKey().getModulus().toString(),
                pair.getPublicKey().getPublicExponent().toString());
        assertNotNull(rebuilt);
        String sign = RSAUtils.sign("payload", pair.getPrivateKey());
        assertTrue(RSAUtils.verify(sign, "payload", rebuilt));
    }

    /**
     * 并发契约（回归静态共享 KeyFactory）：多线程同时做 字符串密钥解析 + 加解密，
     * 任何线程不得因共享 KeyFactory 状态串扰而失败。
     */
    @Test
    void concurrentKeyParsingAndCipher() throws Exception {
        RSAKeyPair pair = keyPair1024();
        String pubWord = RSAUtils.key2Base64(pair.getPublicKey());
        String priWord = RSAUtils.key2Base64(pair.getPrivateKey());
        byte[] plain = "concurrent rsa payload".getBytes(StandardCharsets.UTF_8);
        int threads = 8;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicInteger failures = new AtomicInteger();
        try {
            CyclicBarrier barrier = new CyclicBarrier(threads);
            CountDownLatch done = new CountDownLatch(threads);
            for (int t = 0; t < threads; t++) {
                pool.execute(() -> {
                    try {
                        barrier.await(5, TimeUnit.SECONDS);
                        for (int round = 0; round < 50; round++) {
                            RSAPublicKey pub = RSAUtils.toPublicKey(pubWord);
                            RSAPrivateKey pri = RSAUtils.toPrivateKey(priWord);
                            byte[] encrypted = RSAUtils.encrypt(plain, pub);
                            if (!Arrays.equals(plain, RSAUtils.decrypt(encrypted, pri))) {
                                failures.incrementAndGet();
                            }
                        }
                    } catch (Exception e) {
                        failures.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }
            assertTrue(done.await(60, TimeUnit.SECONDS), "并发 RSA 超时");
        } finally {
            pool.shutdownNow();
        }
        assertEquals(0, failures.get(), "并发 RSA 出现失败");
    }

    /** RSAKeyContainer：随机取对 + 按密钥对象回查 */
    @Test
    void keyContainerLookup() throws Exception {
        RSAKeyContainer container = new RSAKeyContainer(4, 1024);
        RSAKeyPair random = container.getKeyPair();
        assertNotNull(random);
        assertSame(container.getKeyPair(random.getPrivateKey()), random, "私钥对象应能回查到原对");
        assertSame(container.getKeyPair(random.getPublicKey()), random, "公钥对象应能回查到原对");
    }

    /**
     * 非法密钥文本的受控失败——两套入口方向不同（按现状钉桩，不产出"看似可用"的错误密钥）：
     * PKCS#8/X.509 文本解析（toPrivateKey/toPublicKey）上抛 InvalidKeySpecException；
     * 模/指数重建（getPublicKey/getPrivateKey）catch Exception 后 printStackTrace 返回 null。
     */
    @Test
    void illegalKeyTextFailsUnderControl() {
        // 非 Base64 字母被解码器丢弃 → 空编码 → 密钥规格校验显式失败
        assertThrows(InvalidKeySpecException.class, () -> RSAUtils.toPrivateKey("!!!"));
        assertThrows(InvalidKeySpecException.class, () -> RSAUtils.toPublicKey("!!!"));
        // 合法 Base64 但非密钥结构 → 同样是 InvalidKeySpecException（不是返回 null）
        assertThrows(InvalidKeySpecException.class, () -> RSAUtils.toPrivateKey("ABC"));
        // 模/指数入口：非法数值吞异常返 null（该入口不抛受检异常，调用方必须判空）
        assertNull(RSAUtils.getPublicKey("非数字", "x"), "模非法时公钥重建必须返回空");
        assertNull(RSAUtils.getPrivateKey("非数字", "x"), "模非法时私钥重建必须返回空");
    }

    /**
     * 密文结构不符与单段篡改：解密 MUST 显式失败（BadPaddingException），绝不返回截断/拼凑的垃圾。
     * 除真实密文的长度与位篡改外，另钉两处与概率无关的结构性垃圾块（全 0 填充必不过、全 FF 大于模数），
     * 并确认失败不污染密钥对的后续正常往返。
     */
    @Test
    void malformedOrTamperedCiphertextIsRejected() throws Exception {
        RSAKeyPair pair = keyPair1024();
        byte[] single = new byte[100];
        Arrays.fill(single, (byte) 0x5A);
        byte[] oneSegment = RSAUtils.encrypt(single, pair.getPublicKey());
        assertEquals(128, oneSegment.length);

        // 总长不是一段密文（模数字节数）的整数倍：多一字节、少一字节、三段再多三字节
        assertThrows(BadPaddingException.class,
                () -> RSAUtils.decrypt(Arrays.copyOf(oneSegment, 129), pair.getPrivateKey()));
        assertThrows(BadPaddingException.class,
                () -> RSAUtils.decrypt(Arrays.copyOf(oneSegment, 127), pair.getPrivateKey()));
        byte[] threeSegments = RSAUtils.encrypt(new byte[300], pair.getPublicKey());
        assertEquals(384, threeSegments.length, "300 字节明文应为三段 3×128");
        assertThrows(BadPaddingException.class,
                () -> RSAUtils.decrypt(Arrays.copyOf(threeSegments, 387), pair.getPrivateKey()));
        assertThrows(BadPaddingException.class,
                () -> RSAUtils.decrypt(new byte[]{0x01}, pair.getPrivateKey()));

        // 单段字节翻转（首段、尾段各一处）
        byte[] tamperedHead = oneSegment.clone();
        tamperedHead[10] ^= 0x01;
        assertThrows(BadPaddingException.class, () -> RSAUtils.decrypt(tamperedHead, pair.getPrivateKey()));
        byte[] tamperedLastSegment = threeSegments.clone();
        tamperedLastSegment[2 * 128 + 5] ^= 0x40;
        assertThrows(BadPaddingException.class, () -> RSAUtils.decrypt(tamperedLastSegment, pair.getPrivateKey()));

        // 结构性垃圾块（与密钥无关，必然失败）：全 0 段填充校验不过、全 FF 段大于模数
        assertThrows(BadPaddingException.class, () -> RSAUtils.decrypt(new byte[128], pair.getPrivateKey()));
        byte[] allMax = new byte[128];
        Arrays.fill(allMax, (byte) 0xFF);
        assertThrows(BadPaddingException.class, () -> RSAUtils.decrypt(allMax, pair.getPrivateKey()));

        // 失败不得使密钥对进入异常状态：原密文仍可完整解回
        assertArrayEquals(single, RSAUtils.decrypt(oneSegment, pair.getPrivateKey()));
    }

    /**
     * 签名值篡改（改 Base64 串一个字符 / 翻原始字节一位）对原数据验签：明确返回拒绝（false）。
     * 边界方向单独钉桩：改变签名**长度**的篡改走的是 JDK 长度前置校验，抛 SignatureException
     * 而非返回 false——调用方不能指望 boolean 返回值兜住所有篡改形态。
     */
    @Test
    void tamperedSignatureValueIsRejected() throws Exception {
        RSAKeyPair pair = keyPair1024();
        String data = "订单号=T20261001001&金额=648";
        for (String algorithm : new String[]{RSAUtils.SIGN_MD5_ALGORITHM, RSAUtils.SIGN_SHA1_ALGORITHMS}) {
            String sign = RSAUtils.sign(data, pair.getPrivateKey(), algorithm);
            assertTrue(RSAUtils.verify(sign, data, pair.getPublicKey(), algorithm), algorithm + " 未篡改基线必须通过");

            char[] head = sign.toCharArray();
            head[0] = head[0] == 'A' ? 'B' : 'A';
            assertFalse(RSAUtils.verify(new String(head), data, pair.getPublicKey(), algorithm),
                    algorithm + " 签名值首字符被改必须拒绝");
            char[] middle = sign.toCharArray();
            middle[middle.length / 2] = middle[middle.length / 2] == 'x' ? 'y' : 'x';
            assertFalse(RSAUtils.verify(new String(middle), data, pair.getPublicKey(), algorithm),
                    algorithm + " 签名值中段字符被改必须拒绝");
            assertFalse(RSAUtils.verify(new String(head), data, RSAUtils.key2Base64(pair.getPublicKey()), algorithm),
                    algorithm + " 密钥文本入口同样拒绝签名篡改");

            byte[] raw = Base64.decodeBase64(sign);
            assertEquals(128, raw.length, algorithm + " 签名原始长度应等于一个模数字节数");
            byte[] flipped = raw.clone();
            flipped[64] ^= 0x08;
            assertFalse(RSAUtils.verify(flipped, data.getBytes(StandardCharsets.UTF_8), pair.getPublicKey(), algorithm),
                    algorithm + " 字节通道翻位必须拒绝");
            byte[] shortened = Arrays.copyOf(raw, raw.length / 2);
            assertThrows(SignatureException.class,
                    () -> RSAUtils.verify(shortened, data.getBytes(StandardCharsets.UTF_8), pair.getPublicKey(), algorithm),
                    algorithm + " 签名长度不符必须显式失败而非返回 false");
        }
    }

    /**
     * 多组密钥对交错：两组密钥各自加密/解密成立，交叉方向（对象入口与密钥文本入口、公钥方向）
     * 一律显式失败；他组公钥不得验通本组签名。
     */
    @Test
    void interleavedKeyPairsStayIsolated() throws Exception {
        RSAKeyPair first = keyPair1024();
        RSAKeyPair second = keyPair1024();
        byte[] plainA = "payload-A".getBytes(StandardCharsets.UTF_8);
        byte[] plainB = new byte[300]; // 跨三段，检验多段密文的交叉方向
        new Random(20261001L).nextBytes(plainB);

        byte[] cipherA = RSAUtils.encrypt(plainA, first.getPublicKey());
        byte[] cipherB = RSAUtils.encrypt(plainB, second.getPublicKey());
        assertEquals(128, cipherA.length);
        assertEquals(384, cipherB.length);

        // 本组内往返
        assertArrayEquals(plainA, RSAUtils.decrypt(cipherA, first.getPrivateKey()));
        assertArrayEquals(plainB, RSAUtils.decrypt(cipherB, second.getPrivateKey()));

        // 交叉解密：单段与多段密文都不得被另一组解出
        assertThrows(BadPaddingException.class, () -> RSAUtils.decrypt(cipherA, second.getPrivateKey()));
        assertThrows(BadPaddingException.class, () -> RSAUtils.decrypt(cipherB, first.getPrivateKey()));
        assertThrows(BadPaddingException.class, () -> RSAUtils.decryptByPrivateKey(cipherA,
                RSAUtils.key2Base64(second.getPrivateKey())), "密钥文本入口同样不得跨组解通");
        assertThrows(BadPaddingException.class, () -> RSAUtils.decryptByPublicKey(cipherB,
                RSAUtils.key2Base64(first.getPublicKey())), "公钥方向同样不得跨组解通");

        // 签名不跨组：本组签名交他组公钥验证必须被拒（不通过，且不误判为真）
        String signA = RSAUtils.sign("interleave-payload", first.getPrivateKey());
        assertTrue(RSAUtils.verify(signA, "interleave-payload", first.getPublicKey()), "本组公钥必须验通");
        assertFalse(RSAUtils.verify(signA, "interleave-payload", second.getPublicKey()), "他组公钥不得验通");
    }

    /**
     * 密钥容器未注册对象回查为空（错误路径）：容器外的私钥/公钥都不得误命中，
     * 随机取用必须恒落在注册集内。
     * 注：回查依赖 HashMap 的 equals——JDK 15 起 RSA 密钥按密钥材料实现 equals，
     * 故"同材料的重新解析副本"在本 JDK 上也会命中（JDK 15 之前是身份语义、返回 null）。
     * 该分支随 JDK 版本而变，本用例不钉桩，只钉"容器外对象返回空"这一版本无关方向。
     */
    @Test
    void unregisteredKeyLookupReturnsNull() throws Exception {
        RSAKeyContainer container = new RSAKeyContainer(3, 1024);
        RSAKeyPair outsider = keyPair1024();
        assertNull(container.getKeyPair(outsider.getPrivateKey()), "容器外私钥不得误命中");
        assertNull(container.getKeyPair(outsider.getPublicKey()), "容器外公钥不得误命中");
        for (int i = 0; i < 20; i++) {
            RSAKeyPair picked = container.getKeyPair();
            assertSame(container.getKeyPair(picked.getPrivateKey()), picked, "随机取用必须是注册对之一（私钥回查）");
            assertSame(container.getKeyPair(picked.getPublicKey()), picked, "随机取用必须是注册对之一（公钥回查）");
        }
    }

}

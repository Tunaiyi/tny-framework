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
package com.tny.game.common.digest;

import com.tny.game.common.digest.aes.AESCipher;
import com.tny.game.common.digest.blowfish.BlowfishCipher;
import com.tny.game.common.digest.md5.MD5;
import org.junit.jupiter.api.*;

import javax.crypto.BadPaddingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 对称密码与摘要的往返/向量契约：
 * AES/CBC/PKCS5（固定 IV 与自动 IV 两条初始化路径）、Blowfish/ECB/PKCS5、
 * BaseCipher 的 String 通道（URL-safe Base64 + UTF-8，修复了默认字符集不对称）、
 * CipherHolder 的 ThreadLocal 隔离（含错钥失败后通道残余状态、跨线程密文交接），以及 MD5 标准向量。
 */
class CipherAndDigestTest {

    private static final String KEY_16 = "0123456789abcdef";
    private static final String IV_16 = "fedcba9876543210";

    // ---- MD5 标准向量 ----

    @Test
    void md5KnownVectors() {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", MD5.md5(""));
        assertEquals("5d41402abc4b2a76b9719d911017c592", MD5.md5("hello"));
        assertEquals(MD5.md5("hello"), MD5.md5("hello".getBytes(StandardCharsets.UTF_8)),
                "String 与 byte 通道必须一致");
    }

    // ---- AES 固定 IV ----

    @Test
    void aesCbcFixedIvRoundTrip() throws Exception {
        AESCipher cipher = new AESCipher(KEY_16, IV_16);
        for (byte[] plain : new byte[][]{
                {},
                {0x42},
                new byte[16],           // 恰好一个分组
                new byte[17],           // 跨分组触发 PKCS5 补一整块
                "你好，游戏服务器".getBytes(StandardCharsets.UTF_8),
        }) {
            byte[] encrypted = cipher.encrypt(plain.clone());
            assertArrayEquals(plain, cipher.decrypt(encrypted), Arrays.toString(plain) + " AES 往返丢失");
        }
    }

    /** 固定 IV + CBC：同明文两次加密必须逐字节一致（IV 不随 init 重置丢失） */
    @Test
    void aesCbcFixedIvIsDeterministic() throws Exception {
        AESCipher cipher = new AESCipher(KEY_16.getBytes(StandardCharsets.UTF_8), IV_16);
        byte[] plain = "deterministic-payload!!".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(cipher.encrypt(plain), cipher.encrypt(plain));
    }

    /**
     * 契约陷阱记录：AES/CBC 无 IV 无法构造——BaseCipher 构造函数即预热 Cipher，
     * SunJCE 对 CBC 强制参数（InvalidKeyException: Parameters missing），
     * "ivParameter 可为空"的构造签名是误导（建议后续改造为懒初始化或自动生成 IV）。
     */
    @Test
    void aesCbcWithoutIvFailsAtConstruction() {
        assertThrows(java.security.InvalidKeyException.class, () -> new AESCipher(KEY_16, null));
    }

    /** 无 IV 场景的可用通道：AES/ECB 构造 + 往返 + 确定性 */
    @Test
    void aesEcbRoundTrip() throws Exception {
        AESCipher cipher = new AESCipher(KEY_16, null, "AES/ECB/PKCS5Padding");
        byte[] plain = "ecb payload!!".getBytes(StandardCharsets.UTF_8);
        byte[] first = cipher.encrypt(plain.clone());
        assertArrayEquals(plain, cipher.decrypt(first));
        assertArrayEquals(first, cipher.encrypt(plain.clone()), "ECB 同明文应同密文");
    }

    // ---- String 通道：URL-safe Base64 + UTF-8 ----

    @Test
    void stringChannelRoundTrip() throws Exception {
        AESCipher cipher = new AESCipher(KEY_16, IV_16);
        for (String text : new String[]{"", "plain ascii", "中文与emoji 😀", "{}?=&:/+#"}) {
            String encrypted = cipher.encrypt(text);
            assertFalse(encrypted.contains("+") || encrypted.contains("/"),
                    "必须是 URL-safe Base64（可含 = 填充，但不得含 + /，否则需二次转义）: " + encrypted);
            assertEquals(text, cipher.decrypt(encrypted));
        }
    }

    /** 跨线程往返：CipherHolder 每线程独立 Cipher，结果可互相解开 */
    @Test
    void threadLocalCiphersAreIsolated() throws Exception {
        AESCipher cipher = new AESCipher(KEY_16, IV_16);
        int threads = 6;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicInteger failures = new AtomicInteger();
        try {
            CyclicBarrier barrier = new CyclicBarrier(threads);
            CountDownLatch done = new CountDownLatch(threads);
            for (int t = 0; t < threads; t++) {
                final int task = t;
                pool.execute(() -> {
                    try {
                        barrier.await(5, TimeUnit.SECONDS);
                        for (int round = 0; round < 100; round++) {
                            String text = "thread-" + task + "-round-" + round;
                            String back = cipher.decrypt(cipher.encrypt(text));
                            if (!text.equals(back)) {
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
            assertTrue(done.await(60, TimeUnit.SECONDS), "并发加解密超时");
        } finally {
            pool.shutdownNow();
        }
        assertEquals(0, failures.get(), "ThreadLocal Cipher 出现互相污染");
    }

    // ---- Blowfish ECB ----

    @Test
    void blowfishEcbRoundTrip() throws Exception {
        BlowfishCipher cipher = new BlowfishCipher("blowfish-secret-key");
        for (byte[] plain : new byte[][]{{}, {1}, new byte[8], new byte[9],
                "blowfish 中文".getBytes(StandardCharsets.UTF_8)}) {
            assertArrayEquals(plain, cipher.decrypt(cipher.encrypt(plain.clone())));
        }
        // ECB + 同密钥：确定性
        byte[] fixed = "same-input".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(cipher.encrypt(fixed), cipher.encrypt(fixed));
    }

    /**
     * 错误密钥解密必须失败（PKCS5 填充校验），不得静默返回垃圾；且失败不留残余——
     * 错钥通道重试同样显式失败（不会被"洗白"），正钥通道仍可把同一密文完整解回，
     * 固定 IV 也未被失败推进（重新加密同明文仍得同密文）。
     */
    @Test
    void wrongKeyCannotDecrypt() throws Exception {
        AESCipher aes = new AESCipher(KEY_16, IV_16);
        AESCipher other = new AESCipher("different-16byte", IV_16);
        byte[] plain = "secret payload".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aes.encrypt(plain.clone());
        assertThrows(BadPaddingException.class, () -> other.decrypt(encrypted));
        assertThrows(BadPaddingException.class, () -> other.decrypt(encrypted), "错钥通道重试仍须显式失败");
        assertArrayEquals(plain, aes.decrypt(encrypted), "正钥通道在错钥失败后仍须解回同一密文");
        assertArrayEquals(encrypted, aes.encrypt(plain), "失败不得使正钥通道的固定 IV 前移");
    }

    /**
     * 跨线程密文交接（CipherHolder 每线程独立句柄的错误路径）：线程 A 经同一实例 encrypt 产出的密文，
     * 交线程 B 用同一实例 decrypt 必须逐字还原。栅栏同起 + 每一跳都有界等待，杜绝"挂死即视为通过"。
     */
    @Test
    void ciphertextHandoffAcrossThreads() throws Exception {
        AESCipher cipher = new AESCipher(KEY_16, IV_16);
        int rounds = 20;
        String[] texts = new String[rounds];
        for (int i = 0; i < rounds; i++) {
            texts[i] = "handoff-" + i + "-你好，游戏服务器";
        }
        ExecutorService pool = Executors.newFixedThreadPool(2);
        AtomicInteger failures = new AtomicInteger();
        AtomicReference<String> writerThread = new AtomicReference<>();
        AtomicReference<String> readerThread = new AtomicReference<>();
        try {
            CyclicBarrier barrier = new CyclicBarrier(2);
            SynchronousQueue<String> toReader = new SynchronousQueue<>();
            SynchronousQueue<Boolean> backToWriter = new SynchronousQueue<>();
            Future<?> writer = pool.submit(() -> {
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                    writerThread.set(Thread.currentThread().getName());
                    for (String text : texts) {
                        if (!toReader.offer(cipher.encrypt(text), 10, TimeUnit.SECONDS)) {
                            failures.incrementAndGet();
                            return;
                        }
                        if (!Boolean.TRUE.equals(backToWriter.poll(10, TimeUnit.SECONDS))) {
                            failures.incrementAndGet();
                            return;
                        }
                    }
                } catch (Exception e) {
                    failures.incrementAndGet();
                }
            });
            Future<?> reader = pool.submit(() -> {
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                    readerThread.set(Thread.currentThread().getName());
                    for (int i = 0; i < rounds; i++) {
                        String encrypted = toReader.poll(10, TimeUnit.SECONDS);
                        if (encrypted == null) {
                            failures.incrementAndGet();
                            return;
                        }
                        boolean restored;
                        try {
                            restored = texts[i].equals(cipher.decrypt(encrypted));
                        } catch (Exception e) {
                            restored = false;
                        }
                        if (!restored) {
                            failures.incrementAndGet();
                        }
                        if (!backToWriter.offer(restored, 10, TimeUnit.SECONDS)) {
                            failures.incrementAndGet();
                            return;
                        }
                    }
                } catch (Exception e) {
                    failures.incrementAndGet();
                }
            });
            writer.get(60, TimeUnit.SECONDS);
            reader.get(60, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }
        assertNotNull(writerThread.get(), "写入线程未起跑");
        assertNotNull(readerThread.get(), "读取线程未起跑");
        assertNotEquals(writerThread.get(), readerThread.get(), "交接必须真正跨线程发生");
        assertEquals(0, failures.get(), "跨线程交接出现失败或内容不符");
    }

}

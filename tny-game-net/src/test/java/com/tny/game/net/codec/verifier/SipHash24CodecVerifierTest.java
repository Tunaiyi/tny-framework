/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.codec.verifier;

import com.tny.game.common.digest.*;
import com.tny.game.common.digest.binary.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * add-mac-generation-siphash 2.1：SipHash 认证代次校验器契约。
 * 算法正确性已由 SipHash24Test 官方向量锚定；本测试锚定"输入构造"：
 * MAC = SipHash24(ak[0..8], ak[8..16], number4 ‖ body窗口 ‖ ak ‖ code4)，
 * 与生产 CRC64CodecVerifier 的混入序列完全同构（设计决策 1）。
 */
class SipHash24CodecVerifierTest {

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }

    /** 独立重算（不经被测 verifier 的输入组装逻辑，用已锚定的 SipHash24 直接合成） */
    private static byte[] referenceMac(DataPackageContext packager, byte[] body, int offset, int length) {
        byte[] ak = packager.getAccessKeyBytes();
        SipHash24 mac = new SipHash24(le64(ak, 0), le64(ak, 8));
        mac.update(BytesAide.int2Bytes(packager.getPacketNumber()), 0, 4);
        mac.update(body, offset, length);
        mac.update(ak, 0, ak.length);
        mac.update(BytesAide.int2Bytes(packager.getPacketCode()), 0, 4);
        return BytesAide.long2Bytes(mac.digest());
    }

    private static DataPackageContext packager() {
        DataPackCodecOptions config = new DataPackCodecOptions();
        config.setSecurityKeys(new String[]{"bench-mac-gen-key"});
        return new DataPackageContext(999999L, config);
    }

    /** (a) 混入序列同构 + 码长 8B */
    @Test
    void macInputMirrorsCrc64Sequence() {
        SipHash24CodecVerifier verifier = new SipHash24CodecVerifier();
        assertEquals(8, verifier.getCodeLength());
        DataPackageContext packager = packager();
        byte[] page = new byte[133];
        new Random(9).nextBytes(page);
        packager.nextNumber();

        byte[] produced = verifier.generate(packager, page, 5, 120);
        assertArrayEquals(referenceMac(packager, page, 5, 120), produced,
                "MAC 输入构造必须为 number‖body窗口‖ak‖code 同构序列");
        assertTrue(verifier.verify(packager, page, 5, 120, produced));
    }

    /** (b) 篡改载荷/包号/校验码 → 拒绝；全不匹配与单字节不匹配行为一致 */
    @Test
    void tamperingIsRejected() {
        SipHash24CodecVerifier verifier = new SipHash24CodecVerifier();
        DataPackageContext packager = packager();
        byte[] body = new byte[64];
        new Random(3).nextBytes(body);
        packager.nextNumber();
        byte[] mac = verifier.generate(packager, body, 0, body.length);

        byte[] tamperedBody = body.clone();
        tamperedBody[31] ^= 0x5A;
        assertFalse(verifier.verify(packager, tamperedBody, 0, tamperedBody.length, mac), "改载荷必须拒");

        byte[] tamperedMac = mac.clone();
        tamperedMac[0] ^= 0x01;
        assertFalse(verifier.verify(packager, body, 0, body.length, tamperedMac), "改单 bit 校验码必须拒");
        assertFalse(verifier.verify(packager, body, 0, body.length, new byte[8]), "全不匹配校验码必须拒");

        DataPackageContext advanced = packager();
        advanced.nextNumber();
        advanced.nextNumber();
        assertFalse(verifier.verify(advanced, body, 0, body.length, mac), "换包号（number 参与 MAC）必须拒");
    }

    /** (c) 共享单实例多线程交错使用安全（FastThreadLocal 线程封闭回归，复刻 fix-xor 红基线教训） */
    @Test
    void sharedVerifierIsThreadSafe() throws Exception {
        SipHash24CodecVerifier verifier = new SipHash24CodecVerifier();
        int threads = 4;
        int rounds = 2000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier barrier = new CyclicBarrier(threads);
        CountDownLatch done = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            final int salt = t;
            pool.execute(() -> {
                try {
                    DataPackageContext packager = packager();
                    byte[] body = new byte[40 + salt * 7];
                    new Random(salt).nextBytes(body);
                    barrier.await();
                    for (int i = 0; i < rounds; i++) {
                        packager.nextNumber();
                        byte[] mac = verifier.generate(packager, body, 0, body.length);
                        if (!verifier.verify(packager, body, 0, body.length, mac)) {
                            throw new AssertionError("线程 " + Thread.currentThread() + " 第 " + i + " 轮自校验失败：实例状态被跨线程污染");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    done.countDown();
                }
            });
        }
        assertTrue(done.await(60, TimeUnit.SECONDS), "并发回归超时");
        pool.shutdownNow();
    }

    /** (d) 默认 legacy 装配（CRC64 + XOr）行为回归冒烟：新代次合入不动默认路径 */
    @Test
    void legacyDefaultPathUnaffected() {
        CRC64CodecVerifier crc64 = new CRC64CodecVerifier();
        DataPackageContext packager = packager();
        byte[] body = "legacy-payload".getBytes(StandardCharsets.UTF_8);
        packager.nextNumber();
        byte[] code = crc64.generate(packager, body, 0, body.length);
        assertTrue(crc64.verify(packager, body, 0, body.length, code), "CRC64 默认路径必须保持原行为");
        byte[] xored = new XOrCodecCrypto().encrypt(packager, body.clone(), 0, body.length);
        assertFalse(Arrays.equals(body, xored), "XOr 默认仍执行混淆");
    }
}

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

import com.tny.game.common.digest.binary.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.zip.CRC32;

import static org.junit.jupiter.api.Assertions.*;

/**
 * add-crc32-verify-tier 1.1：快筛代次校验器契约。
 * 锚定两件事：IEEE 标准多项式（JDK java.util.zip.CRC32 即标准实现，以金样 0xCBF43926 自证）
 * 与"number‖body‖accessKey‖code"同构输入构造（设计决策 3）——防伪能力 NOT 承诺（规格 Scenario 5）。
 */
class Crc32CodecVerifierTest {

    /** IEEE CRC32 标准金样（跨端对表基准，.NET System.IO.Hashing.Crc32 同值） */
    @Test
    void jdkCrc32IsStandardIeee() {
        CRC32 crc = new CRC32();
        crc.update("123456789".getBytes(StandardCharsets.US_ASCII));
        assertEquals(0xCBF43926L, crc.getValue(), "JDK CRC32 必须是 IEEE 标准多项式（快筛档跨端零移植的依据）");
    }

    /** (a) 混入序列同构 + 码长 4B：以测试内独立参考链（同 JDK 类、同序列）全等比对 */
    @Test
    void macInputMirrorsCrc64SequenceWithFourByteCode() {
        Crc32CodecVerifier verifier = new Crc32CodecVerifier();
        assertEquals(4, verifier.getCodeLength(), "快筛代次校验码 4B——帧尾较 8B 档省 4 字节");
        DataPackageContext packager = packager();
        byte[] page = new byte[140];
        new Random(9).nextBytes(page);
        packager.nextNumber();

        byte[] produced = verifier.generate(packager, page, 6, 121);
        assertArrayEquals(referenceCode(packager, page, 6, 121), produced,
                "输入构造必须为 number‖body窗口‖ak‖code 同构序列");
        assertTrue(verifier.verify(packager, page, 6, 121, produced));
    }

    /** (b) 载荷/包号/密钥材料任一变化 → 拒绝 */
    @Test
    void sensitivityToPayloadNumberAndKey() {
        Crc32CodecVerifier verifier = new Crc32CodecVerifier();
        DataPackageContext packager = packager();
        byte[] body = "crc32-tier-payload".getBytes(StandardCharsets.UTF_8);
        packager.nextNumber();
        byte[] code = verifier.generate(packager, body, 0, body.length);

        byte[] tampered = body.clone();
        tampered[3] ^= 0x40;
        assertFalse(verifier.verify(packager, tampered, 0, tampered.length, code), "改载荷必须拒");

        DataPackageContext advanced = packager();
        advanced.nextNumber();
        advanced.nextNumber();
        assertFalse(verifier.verify(advanced, body, 0, body.length, code), "换包号必须拒");

        // accessKey 参与混入：不同 accessId 派生的连接对同一码验不过
        Crc32CodecVerifier same = new Crc32CodecVerifier();
        DataPackageContext foreign = new DataPackageContext(123456L, options());
        foreign.nextNumber();
        assertFalse(same.verify(foreign, body, 0, body.length, code), "换 accessId（ak 参与混入）必须拒");
    }

    /** (c) 共享单实例多线程交错安全（ThreadLocal 线程封闭回归） */
    @Test
    void sharedVerifierIsThreadSafe() throws Exception {
        Crc32CodecVerifier verifier = new Crc32CodecVerifier();
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
                    byte[] body = new byte[40 + salt * 9];
                    new Random(salt).nextBytes(body);
                    barrier.await();
                    for (int i = 0; i < rounds; i++) {
                        packager.nextNumber();
                        byte[] code = verifier.generate(packager, body, 0, body.length);
                        if (!verifier.verify(packager, body, 0, body.length, code)) {
                            throw new AssertionError("线程 " + Thread.currentThread() + " 第 " + i + " 轮自校验失败：实例状态跨线程污染");
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

    /** (d) 默认代次（CRC64）路径冒烟：新类在 classpath 不影响默认行为 */
    @Test
    void legacyDefaultUnaffected() {
        CRC64CodecVerifier crc64 = new CRC64CodecVerifier();
        DataPackageContext packager = packager();
        byte[] body = "legacy-path".getBytes(StandardCharsets.UTF_8);
        packager.nextNumber();
        byte[] code = crc64.generate(packager, body, 0, body.length);
        assertTrue(crc64.verify(packager, body, 0, body.length, code));
        byte[] xored = new XOrCodecCrypto().encrypt(packager, body.clone(), 0, body.length);
        assertFalse(Arrays.equals(body, xored));
    }

    private static DataPackCodecOptions options() {
        DataPackCodecOptions config = new DataPackCodecOptions();
        config.setSecurityKeys(new String[]{"bench-crc32-tier-key"});
        return config;
    }

    private static DataPackageContext packager() {
        return new DataPackageContext(999999L, options());
    }

    private static byte[] referenceCode(DataPackageContext packager, byte[] body, int offset, int length) {
        CRC32 crc = new CRC32();
        crc.reset();
        crc.update(BytesAide.int2Bytes(packager.getPacketNumber()));
        crc.update(body, offset, length);
        byte[] ak = packager.getAccessKeyBytes();
        crc.update(ak);
        crc.update(BytesAide.int2Bytes(packager.getPacketCode()));
        return BytesAide.int2Bytes((int) crc.getValue());
    }
}

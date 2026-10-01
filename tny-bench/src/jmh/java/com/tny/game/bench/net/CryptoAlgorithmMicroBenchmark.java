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
package com.tny.game.bench.net;

import com.tny.game.common.digest.binary.*;
import com.tny.game.net.codec.verifier.*;
import org.openjdk.jmh.annotations.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32C;

/**
 * 校验/加密算法逐步成本对比（零 copy 与 verify 税课题的算法侧证据）。
 * 对比对象与生产实现锚定：CRC64CodecVerifier.doGenerate 的分配形态（每包 7 对象）
 * 与 XOrCodecCrypto/BytesAide.xor 的逐字节取模形态为现状基线；
 * slicing-by-8 / CRC32C intrinsic / tile+long XOR / JCE ChaCha20 / AES-GCM / ChaCha20-Poly1305 / HMAC-SHA256 为候选。
 * 参数对齐 baseline：-f 2 -wi 3 -i 5 -w 500ms -r 1s（CLI 传入）；-prof gc 出分配归因。
 * 等价性防线：@Setup 断言候选实现与生产实现逐字节一致，不一致直接跑崩基准（数字必须长在对的东西上）。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class CryptoAlgorithmMicroBenchmark {

    static final long CRC64_INITIAL = 0xFFFFFFFFFFFFFFFFL;

    @Param({"96", "1024"})
    private int size;

    private byte[] body;
    private byte[] accessKey;   // 生产同款 16B（md5 派生）
    private byte[] secKey;      // pack security key（本基准固定 32B，覆盖 lcm(sec,4)=32 的周期）
    private final byte[] num4 = new byte[4];
    private final byte[] code4 = new byte[4];
    private final byte[] out8 = new byte[8];
    private final byte[] macOut = new byte[32];
    private int counter;

    private Cipher chachaCipher;
    private byte[] chachaKey;      // 16B：ChaCha20 密钥流
    private byte[] chachaPolyKey;  // 32B：ChaCha20-Poly1305 要求 256-bit
    private Cipher gcmCipher;
    private SecretKeySpec gcmKeySpec;
    private Cipher polyCipher;
    private SecretKeySpec chachaKeySpec;
    private SecretKeySpec polyKeySpec;
    private Mac hmac;
    private byte[] ctScratch;
    private byte[] decScratch;
    private Crc64Slicing slicing;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        Random random = new Random(42);
        body = new byte[size];
        random.nextBytes(body);
        accessKey = new byte[16];
        random.nextBytes(accessKey);
        secKey = new byte[32];
        random.nextBytes(secKey);
        ctScratch = new byte[size + 64];
        decScratch = new byte[size];

        slicing = new Crc64Slicing();
        assertSlicingEquivalent(random);
        assertXorTileEquivalent(random);

        chachaKey = "bench-chacha-key".getBytes(StandardCharsets.UTF_8);   // 16B
        chachaPolyKey = new byte[32];
        random.nextBytes(chachaPolyKey);
        byte[] aesKey = new byte[16];
        random.nextBytes(aesKey);
        chachaKeySpec = new SecretKeySpec(Arrays.copyOf(chachaKey, 32), "ChaCha20"); // JDK 要求 256-bit
        polyKeySpec = new SecretKeySpec(chachaPolyKey, "ChaCha20");
        gcmKeySpec = new SecretKeySpec(aesKey, "AES");
        chachaCipher = Cipher.getInstance("ChaCha20");
        gcmCipher = Cipher.getInstance("AES/GCM/NoPadding");
        polyCipher = Cipher.getInstance("ChaCha20-Poly1305");
        probeChaChaMode();
        initChaCha(Cipher.ENCRYPT_MODE, 1, 2);
        hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(chachaKey, "HmacSHA256"));
    }

    /**
     * slicing-by-8 原型断言：只与"标准 CRC64（逻辑右移语义）"自洽——
     * 已实测证明生产 CRC64 使用算术右移 >>（CRC64.java:34,63），sign-mask 破坏
     * 分块并行代数，slicing 与之**不等价**（复现：/tmp/CrcFix2 300 随机样本 mismatch）。
     * 因此本方法产出的吞吐是"若协议换成标准 CRC64"的假设上界，不是 drop-in；
     * drop-in 加速见 crc64ByteNoAlloc（复用生产静态方法）。
     */
    private void assertSlicingEquivalent(Random random) {
        for (int round = 0; round < 64; round++) {
            int off = random.nextInt(8);
            int len = 1 + random.nextInt(Math.max(1, body.length - off));
            long expected = slicing.updateSlow(CRC64_INITIAL, num4, 0, 4);
            expected = slicing.updateSlow(expected, body, off, len);
            expected = slicing.updateSlow(expected, accessKey, 0, 16);
            expected = slicing.updateSlow(expected, code4, 0, 4);
            long actual = slicing.update(CRC64_INITIAL, num4, 0, 4);
            actual = slicing.update(actual, body, off, len);
            actual = slicing.update(actual, accessKey, 0, 16);
            actual = slicing.update(actual, code4, 0, 4);
            if (expected != actual) {
                throw new AssertionError("slicing-by-8 not self-equivalent at round " + round);
            }
        }
    }

    /** tile+long XOR 必须与 BytesAide.xor(security, code) 双键流全等（含非对齐尾部） */
    private void assertXorTileEquivalent(Random random) {
        for (int round = 0; round < 64; round++) {
            int off = random.nextInt(4);
            int len = 1 + random.nextInt(Math.max(1, body.length - off));
            byte[] expectedSrc = Arrays.copyOfRange(body, off, off + len);
            BytesAide.xor(expectedSrc, 0, len, secKey, code4);
            byte[] tile = buildXorTile();
            byte[] actual = Arrays.copyOfRange(body, off, off + len);
            xorTile(actual, 0, len, tile);
            if (!Arrays.equals(expectedSrc, actual)) {
                throw new AssertionError("xor tile not equivalent at round " + round);
            }
        }
    }

    /** JDK19+/新 Spec：nonce 12B；旧版（JDK11~18）：nonce 8B+counter。构造器即抛 IAE，无需试 init */
    private int chachaNonceMode = 12;

    private void probeChaChaMode() {
        try {
            new ChaCha20ParameterSpec(new byte[12], 0);
            chachaNonceMode = 12;
        } catch (IllegalArgumentException legacy) {
            chachaNonceMode = 8;
        }
    }

    private void initChaCha(int mode, int number, int code) throws Exception {
        byte[] iv = buildIv(number, code);
        if (chachaNonceMode == 12) {
            chachaCipher.init(mode, chachaKeySpec, new ChaCha20ParameterSpec(iv, 0));
        } else {
            chachaCipher.init(mode, chachaKeySpec, new ChaCha20ParameterSpec(Arrays.copyOf(iv, 8), code));
        }
    }

    private static byte[] buildIv(int number, int code) {
        byte[] iv = new byte[12];
        BytesAide.int2Bytes(number, iv, 0);
        BytesAide.int2Bytes(code, iv, 4);
        iv[8] = 0x7d; iv[9] = (byte) 0x9f; iv[10] = 0x33; iv[11] = 0x51; // 会话盐（基准固定）
        return iv;
    }

    private byte[] buildXorTile() {
        int period = lcm(secKey.length, 4);
        byte[] tile = new byte[period];
        for (int i = 0; i < period; i++) {
            tile[i] = (byte) (secKey[i % secKey.length] ^ code4[i & 3]);
        }
        return tile;
    }

    private static int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }

    private static int gcd(int a, int b) {
        while (b != 0) { int t = a % b; a = b; b = t; }
        return a;
    }

    private static void xorTile(byte[] data, int off, int len, byte[] tile) {
        int period = tile.length;
        long[] tileLongs = new long[period >>> 3];
        for (int k = 0; k < tileLongs.length; k++) {
            tileLongs[k] = le64(tile, k << 3);
        }
        int i = off;
        int blocks = len / 8;
        for (int n = 0; n < blocks; n++, i += 8) {
            long v = le64(data, i) ^ tileLongs[((i - off) >>> 3) % tileLongs.length];
            data[i] = (byte) v;
            data[i + 1] = (byte) (v >>> 8);
            data[i + 2] = (byte) (v >>> 16);
            data[i + 3] = (byte) (v >>> 24);
            data[i + 4] = (byte) (v >>> 32);
            data[i + 5] = (byte) (v >>> 40);
            data[i + 6] = (byte) (v >>> 48);
            data[i + 7] = (byte) (v >>> 56);
        }
        for (; i < off + len; i++) {
            data[i] ^= tile[(i - off) % period];
        }
    }

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }

    // ==================== 现状基线 ====================

    /** 生产 CRC64CodecVerifier.doGenerate 的逐字复刻：每包 int2Bytes×2 + wrap×4 + long2Bytes×1 */
    @Benchmark
    public byte[] crc64Current() {
        int number = ++counter;
        int code = number * 31 + 7;
        BytesAide.int2Bytes(code, code4, 0);
        return BytesAide.long2Bytes(CRC64.crc64Long(
                ByteBuffer.wrap(BytesAide.int2Bytes(number)),
                ByteBuffer.wrap(body),
                ByteBuffer.wrap(accessKey),
                ByteBuffer.wrap(BytesAide.int2Bytes(code))));
    }

    /** 生产 XOrCodecCrypto 逐字复刻：逐字节双键取模 */
    @Benchmark
    public int xorCurrent() {
        int code = counter * 31 + 7;
        BytesAide.int2Bytes(code, code4, 0);
        counter++;
        BytesAide.xor(body, 0, body.length, secKey, code4);
        return body[0];
    }

    // ==================== 候选：校验 ====================

    /** 去分配 + 逐字节表驱动（隔离“只杀分配”的收益） */
    @Benchmark
    public byte[] crc64ByteNoAlloc() {
        int number = ++counter;
        int code = number * 31 + 7;
        BytesAide.int2Bytes(number, num4, 0);
        BytesAide.int2Bytes(code, code4, 0);
        long crc = CRC64.crc64Long(CRC64_INITIAL, num4, 0, 4);
        crc = CRC64.crc64Long(crc, body, 0, body.length);
        crc = CRC64.crc64Long(crc, accessKey, 0, 16);
        crc = CRC64.crc64Long(crc, code4, 0, 4);
        return BytesAide.long2Bytes(crc, out8, 0);
    }

    /** 去分配 + slicing-by-8 */
    @Benchmark
    public byte[] crc64Slicing8() {
        int number = ++counter;
        int code = number * 31 + 7;
        BytesAide.int2Bytes(number, num4, 0);
        BytesAide.int2Bytes(code, code4, 0);
        long crc = slicing.update(CRC64_INITIAL, num4, 0, 4);
        crc = slicing.update(crc, body, 0, body.length);
        crc = slicing.update(crc, accessKey, 0, 16);
        crc = slicing.update(crc, code4, 0, 4);
        return BytesAide.long2Bytes(crc, out8, 0);
    }

    /** java.util.zip.CRC32C（硬件 intrinsic），keyed 混入形态同生产；校验码天然 4B（帧预算再省 4B） */
    @Benchmark
    public long crc32cKeyed() {
        int number = ++counter;
        int code = number * 31 + 7;
        BytesAide.int2Bytes(number, num4, 0);
        BytesAide.int2Bytes(code, code4, 0);
        CRC32C crc = this.crc32c;
        crc.reset();
        crc.update(num4);
        crc.update(body);
        crc.update(accessKey);
        crc.update(code4);
        return crc.getValue();
    }

    private CRC32C crc32c;

    {
        crc32c = new CRC32C();
    }

    /** HMAC-SHA256 截断 8B：gen+verify 往返（真实派发方向两端都付），键控强度为真 MAC */
    @Benchmark
    public boolean hmacSha256GenVerify() throws Exception {
        int number = ++counter;
        int code = number * 31 + 7;
        BytesAide.int2Bytes(number, num4, 0);
        BytesAide.int2Bytes(code, code4, 0);
        hmac.update(num4);
        hmac.update(body);
        hmac.update(accessKey);
        hmac.update(code4);
        hmac.doFinal(macOut, 0);
        hmac.update(num4);
        hmac.update(body);
        hmac.update(accessKey);
        hmac.update(code4);
        hmac.doFinal(decScratch, 0);
        boolean eq = true;
        for (int i = 0; i < 8; i++) {
            eq &= decScratch[i] == macOut[i];
        }
        return eq;
    }

    // ==================== 候选：加密 ====================

    /** tile 预合并键流 + 8 字节对齐 long 异或（强度与现状 XOR 相同，纯速度升级） */
    @Benchmark
    public int xorTile8() {
        int code = counter++ * 31 + 7;
        BytesAide.int2Bytes(code, code4, 0);
        xorTile(body, 0, body.length, buildXorTile());
        return body[0];
    }

    /** JCE ChaCha20 密钥流加密（现代流密码替换重复键异或；IV 由包号+包码派生，两端一致） */
    @Benchmark
    public int chacha20Keystream() throws Exception {
        int number = counter;
        int code = counter++ * 31 + 7;
        initChaCha(Cipher.ENCRYPT_MODE, number, code);
        chachaCipher.doFinal(body, 0, body.length, ctScratch, 0);
        System.arraycopy(ctScratch, 0, body, 0, body.length);
        return body[0];
    }

    /** AES-128-GCM seal+open 往返：加密+认证一步完成（AEAD 终态的成本上界，tag 96-bit） */
    @Benchmark
    public int aesGcmSealOpen() throws Exception {
        int number = ++counter;
        byte[] iv = buildIv(number, number * 31 + 7);
        gcmCipher.init(Cipher.ENCRYPT_MODE, gcmKeySpec, new GCMParameterSpec(96, iv));
        int ctLen = gcmCipher.doFinal(body, 0, body.length, ctScratch, 0);
        gcmCipher.init(Cipher.DECRYPT_MODE, gcmKeySpec, new GCMParameterSpec(96, iv));
        gcmCipher.doFinal(ctScratch, 0, ctLen, decScratch, 0);
        return decScratch[0];
    }

    /** ChaCha20-Poly1305 seal+open 往返（无 AES-NI 环境的 AEAD 备选，tag 128-bit） */
    @Benchmark
    public int chachaPolySealOpen() throws Exception {
        int number = ++counter;
        byte[] iv = buildIv(number, number * 31 + 7);
        polyCipher.init(Cipher.ENCRYPT_MODE, polyKeySpec, new IvParameterSpec(iv));
        int ctLen = polyCipher.doFinal(body, 0, body.length, ctScratch, 0);
        polyCipher.init(Cipher.DECRYPT_MODE, polyKeySpec, new IvParameterSpec(iv));
        polyCipher.doFinal(ctScratch, 0, ctLen, decScratch, 0);
        return decScratch[0];
    }

    @Benchmark
    public int noop() {
        return counter++;
    }

    /**
     * 与生产 CRC64 同多项式（ECMA-182 反射）的 slicing-by-8 原型。
     * 8 张表：t[0] 即逐字节表，t[k][i] = (t[k-1][i] >>> 8) ^ t[0][t[k-1][i] & 0xFF]。
     */
    static final class Crc64Slicing {
        private final long[][] tables = new long[8][256];

        Crc64Slicing() {
            long poly = 0x95AC9329AC4BC9B5L;
            for (int i = 0; i < 256; i++) {
                long part = i;
                for (int j = 0; j < 8; j++) {
                    part = ((part & 1) != 0) ? (part >>> 1) ^ poly : part >>> 1;
                }
                tables[0][i] = part;
            }
            for (int i = 0; i < 256; i++) {
                long c = tables[0][i];
                for (int k = 1; k < 8; k++) {
                    c = (c >>> 8) ^ tables[0][(int) c & 0xFF];
                    tables[k][i] = c;
                }
            }
        }

        /** 本表语义（标准 CRC64，逻辑右移）下的逐字节参照实现 */
        long updateSlow(long crc, byte[] data, int off, int len) {
            for (int i = off, end = off + len; i < end; i++) {
                crc = tables[0][((int) crc ^ data[i]) & 0xFF] ^ (crc >>> 8);
            }
            return crc;
        }

        long update(long crc, byte[] data, int off, int len) {
            int i = off;
            int end = off + len;
            for (; i + 8 <= end; i += 8) {
                crc ^= le64(data, i);
                crc = tables[7][(int) crc & 0xFF] ^ tables[6][(int) (crc >>> 8) & 0xFF]
                        ^ tables[5][(int) (crc >>> 16) & 0xFF] ^ tables[4][(int) (crc >>> 24) & 0xFF]
                        ^ tables[3][(int) (crc >>> 32) & 0xFF] ^ tables[2][(int) (crc >>> 40) & 0xFF]
                        ^ tables[1][(int) (crc >>> 48) & 0xFF] ^ tables[0][(int) (crc >>> 56) & 0xFF];
            }
            for (; i < end; i++) {
                crc = tables[0][((int) crc ^ data[i]) & 0xFF] ^ (crc >>> 8);
            }
            return crc;
        }

        private static long le64(byte[] b, int i) {
            return CryptoAlgorithmMicroBenchmark.le64(b, i);
        }
    }
}

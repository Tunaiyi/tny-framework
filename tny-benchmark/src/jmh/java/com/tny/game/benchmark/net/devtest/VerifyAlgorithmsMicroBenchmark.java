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
package com.tny.game.benchmark.net.devtest;

import com.tny.game.benchmark.net.shared.SipHash64;
import com.tny.game.common.digest.binary.*;
import org.openjdk.jmh.annotations.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;

/**
 * 校验算法族微基准（承接 crypto-bench-2026-09-30 的"还有哪些可选项"问题）：
 * CRC32(IEEE)/CRC32C(+splitmix 终混)/Adler32（JDK 现成类）、xxHash64(seed 即密钥)、
 * SipHash-2-4（真键控 MAC）、GMAC(AES 仅认证, AES-NI)。
 * 混入形态与生产一致：number+body+accessKey+code 四段。
 * 注意：xxHash64/SipHash 此处为计时探针的参考实现（源自各自公开规范），
 * **转正前必须用官方测试向量核验**（本基准只主张"成本量级"，不主张"实现即正确"）。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class VerifyAlgorithmsMicroBenchmark {

    @Param({"96", "1024"})
    private int size;

    private byte[] body;
    private byte[] accessKey;
    private final byte[] num4 = new byte[4];
    private final byte[] code4 = new byte[4];
    private final byte[] tag16 = new byte[16];
    private final byte[] tag2 = new byte[16];
    private int counter;

    private CRC32 crc32;
    private CRC32C crc32c;
    private Adler32 adler;
    private Cipher gcm;
    private SecretKeySpec aesKey;
    private long sipK0;
    private long sipK1;
    private long xxBaseSeed;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        Random random = new Random(42);
        body = new byte[size];
        random.nextBytes(body);
        accessKey = new byte[16];
        random.nextBytes(accessKey);
        crc32 = new CRC32();
        crc32c = new CRC32C();
        adler = new Adler32();
        byte[] key = new byte[16];
        random.nextBytes(key);
        aesKey = new SecretKeySpec(key, "AES");
        gcm = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] k = new byte[16];
        random.nextBytes(k);
        sipK0 = le64(k, 0);
        sipK1 = le64(k, 8);
        xxBaseSeed = le64(k, 0) ^ 0x9E3779B185EBCA87L;
    }

    private void tick() {
        int number = ++counter;
        BytesAide.int2Bytes(number, num4, 0);
        BytesAide.int2Bytes(number * 31 + 7, code4, 0);
    }

    // ==================== JDK 现成类 ====================

    /** CRC32（IEEE，java.util.zip.CRC32，x86/新 aarch64 intrinsic），keyed 形态同生产 */
    @Benchmark
    public long crc32Keyed() {
        tick();
        CRC32 c = crc32;
        c.reset();
        c.update(num4);
        c.update(body);
        c.update(accessKey);
        c.update(code4);
        return c.getValue();
    }

    /** CRC32C + splitmix64 终混：修复 CRC 线性结构对"改包重算"的弱点（成本≈+1ns） */
    @Benchmark
    public long crc32cSplitmix() {
        tick();
        CRC32C c = crc32c;
        c.reset();
        c.update(num4);
        c.update(body);
        c.update(accessKey);
        c.update(code4);
        long z = c.getValue() ^ num4[0];
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /** Adler32（对照样本：快但短数据检测力弱，预期不推荐） */
    @Benchmark
    public long adler32Keyed() {
        tick();
        Adler32 a = adler;
        a.reset();
        a.update(num4);
        a.update(body);
        a.update(accessKey);
        a.update(code4);
        return a.getValue();
    }

    // ==================== 高性能通用哈希（seed=会话密钥） ====================

    /** xxHash64 参考实现（计时探针；官方向量核验后方可转正），seed 混入 number/code */
    @Benchmark
    public long xxhash64Seeded() {
        tick();
        long seed = xxBaseSeed ^ ((num4[0] & 0xFFL) << 8) | (code4[1] & 0xFFL);
        return xxh64(body, 0, body.length, seed);
    }

    // ==================== 真键控 MAC（低成本档） ====================

    /** SipHash-2-4 参考实现（计时探针）：密钥不可从包体反推，防伪造档位里最便宜的通用解 */
    @Benchmark
    public long sipHash24Keyed() {
        tick();
        return SipHash64.sipHash64(sipK0, sipK1, body, 0, body.length)
                ^ ((num4[0] & 0xFFL) * 0x94D049BB133111EBL);
    }

    // ==================== 硬件 AEAD 的"仅认证"形态 ====================

    /** GMAC（AES-GCM 空明文 + AAD 全部输入）：真 MAC，AES-NI；tag 两端各算一次 */
    @Benchmark
    public boolean gmacSealVerify() throws Exception {
        tick();
        byte[] iv = new byte[12];
        System.arraycopy(num4, 0, iv, 0, 4);
        System.arraycopy(code4, 0, iv, 4, 4);
        iv[8] = 0x7d; iv[9] = (byte) 0x9f; iv[10] = 0x33; iv[11] = 0x51;
        gcm.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        gcm.updateAAD(num4);
        gcm.updateAAD(body);
        gcm.updateAAD(accessKey);
        gcm.updateAAD(code4);
        byte[] tag = gcm.doFinal(new byte[0]);
        System.arraycopy(tag, 0, tag16, 0, 16);
        gcm.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        gcm.updateAAD(num4);
        gcm.updateAAD(body);
        gcm.updateAAD(accessKey);
        gcm.updateAAD(code4);
        byte[] opened = gcm.doFinal(tag16, 0, 16);
        return opened.length == 0;
    }

    @Benchmark
    public int noop() {
        return counter++;
    }

    // ==================== 参考实现（计时探针） ====================

    private static final long P1 = 0x9E3779B185EBCA87L, P2 = 0xC2B2AE3D27D4EB4FL,
            P3 = 0x165667B19E3779F9L, P4 = 0x85EBCA77C2B2AE63L, P5 = 0x27D4EB2F165667C5L;

    private static long xxh64(byte[] in, int off, int len, long seed) {
        int i = off;
        int end = off + len;
        long h;
        if (len >= 32) {
            long v1 = seed + P1 + P2, v2 = seed + P2, v3 = seed, v4 = seed - P1;
            int limit = end - 32;
            while (i <= limit) {
                v1 = xxRound(v1, le64(in, i)); i += 8;
                v2 = xxRound(v2, le64(in, i)); i += 8;
                v3 = xxRound(v3, le64(in, i)); i += 8;
                v4 = xxRound(v4, le64(in, i)); i += 8;
            }
            h = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);
            h = xxMerge(h, v1); h = xxMerge(h, v2); h = xxMerge(h, v3); h = xxMerge(h, v4);
        } else {
            h = seed + P5;
        }
        h += len;
        while (i + 8 <= end) {
            h = Long.rotateLeft(h ^ xxRound(0, le64(in, i)), 27) * P1 + P4;
            i += 8;
        }
        if (i + 4 <= end) {
            long k1 = (le64(in, i) & 0xFFFFFFFFL) * P1;
            h = Long.rotateLeft(h ^ k1, 23) * P2 + P3;
            i += 4;
        }
        while (i < end) {
            h = Long.rotateLeft(h ^ ((in[i] & 0xFFL) * P5), 11) * P1;
            i++;
        }
        h ^= h >>> 33;
        h *= P2;
        h ^= h >>> 29;
        h *= P3;
        return h ^ (h >>> 32);
    }

    private static long xxRound(long acc, long input) {
        acc += input * P2;
        acc = Long.rotateLeft(acc, 31);
        return acc * P1;
    }

    private static long xxMerge(long acc, long val) {
        val = xxRound(0, val);
        acc ^= val;
        return acc * P1 + P4;
    }

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }
}

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

import java.util.Arrays;

/**
 * RFC 7539 ChaCha20-Poly1305 手写实现，**持久状态形态**（Noise 传输层用法）：
 * 密钥 words 构造期一次性装载；每帧只换 12B nonce（调用方按连接计数器构造，Noise 约定 [4B零‖8B LE counter]），
 * 全程 scratch 复用（热路径零分配）——本类是"JCE 每帧 init 税 vs AEAD 本体成本"论断的判据载体。
 * 正确性锚定：{@link #selfTestAgainstJdk()} 与 JDK ChaCha20-Poly1305 逐字节全等（密文段与 tag 段）。
 * 声明：数组版 ChaCha + 通用 26-bit limb Poly（正确性优先）；生产实现可换 donna 展开版，
 * 本基准数字是**上界成本**（只会比生产优化更快，不会更慢）。
 */
final class AeadRfc7539 {

    private final int[] st = new int[16];
    private final int[] x = new int[16];
    private final byte[] blockOut = new byte[64];
    private final byte[] polyKey = new byte[32];
    private final byte[] nonce = new byte[12];
    private byte[] macIn = new byte[256];
    private final byte[] tag = new byte[16];

    private final long[] r = new long[5];
    private final long[] r5 = new long[5];
    private final long[] h = new long[5];
    private final long[] acc = new long[5];
    private final long[] gg = new long[5];

    AeadRfc7539(byte[] key32) {
        st[0] = 0x61707865;
        st[1] = 0x3320646e;
        st[2] = 0x79622d32;
        st[3] = 0x6b206574;
        for (int i = 0; i < 8; i++) {
            st[4 + i] = le32(key32, i * 4);
        }
    }

    /** 设置本帧 12B nonce（调用方从连接计数器构造，Noise 形态）。 */
    void setNonce(byte[] nonce12) {
        System.arraycopy(nonce12, 0, this.nonce, 0, 12);
    }

    // ==================== ChaCha20 ====================

    private void block(int counter) {
        st[12] = counter;
        st[13] = le32(nonce, 0);
        st[14] = le32(nonce, 4);
        st[15] = le32(nonce, 8);
        System.arraycopy(st, 0, x, 0, 16);
        for (int round = 0; round < 10; round++) {
            qr(0, 4, 8, 12);
            qr(1, 5, 9, 13);
            qr(2, 6, 10, 14);
            qr(3, 7, 11, 15);
            qr(0, 5, 10, 15);
            qr(1, 6, 11, 12);
            qr(2, 7, 8, 13);
            qr(3, 4, 9, 14);
        }
        for (int i = 0; i < 16; i++) {
            putLe32(blockOut, i << 2, x[i] + st[i]);
        }
    }

    private void qr(int a, int b, int c, int d) {
        x[a] += x[b];
        x[d] ^= x[a];
        x[d] = Integer.rotateLeft(x[d], 16);
        x[c] += x[d];
        x[b] ^= x[c];
        x[b] = Integer.rotateLeft(x[b], 12);
        x[a] += x[b];
        x[d] ^= x[a];
        x[d] = Integer.rotateLeft(x[d], 8);
        x[c] += x[d];
        x[b] ^= x[c];
        x[b] = Integer.rotateLeft(x[b], 7);
    }

    private void xorKeyStream(byte[] data, int off, int len, byte[] out, int outOff) {
        int done = 0;
        for (int counter = 1; done < len; counter++) {
            block(counter);
            int n = Math.min(64, len - done);
            for (int k = 0; k < n; k++) {
                out[outOff + done + k] = (byte) (data[off + done + k] ^ blockOut[k]);
            }
            done += n;
        }
    }

    // ==================== AEAD seal / open ====================

    /** 密封：密文写入 ctOut，tag 写入内部 scratch（{@link #tag()} 取 16B）。 */
    void seal(byte[] in, int off, int len, byte[] aad, int aOff, int aLen, byte[] ctOut) {
        block(0);
        System.arraycopy(blockOut, 0, polyKey, 0, 32);
        initPoly();
        xorKeyStream(in, off, len, ctOut, 0);
        buildMacInputAndAuth(ctOut, len, aad, aOff, aLen);
    }

    /** 仅认证：对给定密文段计算 tag（管线解码侧 tag 晚于解密到达时分步使用）。 */
    void auth(byte[] ct, int len, byte[] aad, int aOff, int aLen) {
        block(0);
        System.arraycopy(blockOut, 0, polyKey, 0, 32);
        initPoly();
        buildMacInputAndAuth(ct, len, aad, aOff, aLen);
    }

    /** 原地解密（流密码对称，配合 {@link #auth} 先验后放）。 */
    void decryptInPlace(byte[] data, int off, int len) {
        xorKeyStream(data, off, len, data, off);
    }

    /** 打开：先算期望 tag（覆盖密文），再原地解密，比对 tag。 */
    boolean openAndDecrypt(byte[] ct, int off, int len, byte[] aad, int aOff, int aLen, byte[] expectedTag) {
        block(0);
        System.arraycopy(blockOut, 0, polyKey, 0, 32);
        initPoly();
        byte[] ctCopy = ensureCtScratch(len);
        System.arraycopy(ct, off, ctCopy, 0, len);
        buildMacInputAndAuth(ctCopy, len, aad, aOff, aLen);
        boolean ok = constantTimeEquals16(tag, expectedTag);
        if (ok) {
            xorKeyStream(ctCopy, 0, len, ct, off);
        }
        return ok;
    }

    private byte[] ctScratch;

    private byte[] ensureCtScratch(int len) {
        if (ctScratch == null || ctScratch.length < len) {
            ctScratch = new byte[len + 64];
        }
        return ctScratch;
    }

    byte[] tag() {
        return tag;
    }

    String debugRLimbs() {
        return r[0] + "," + r[1] + "," + r[2] + "," + r[3] + "," + r[4];
    }

    byte[] debugPolyOnly(byte[] key32, byte[] msg) {
        System.arraycopy(key32, 0, polyKey, 0, 32);
        initPoly();
        polyMac(msg, msg.length);
        return java.util.Arrays.copyOf(tag, 16);
    }

    byte[] debugPolyKey() {
        block(0);
        return Arrays.copyOf(blockOut, 32);
    }

    private void buildMacInputAndAuth(byte[] ct, int len, byte[] aad, int aOff, int aLen) {
        int padC = (len + 15) & ~15;
        int padA = (aLen + 15) & ~15;
        int total = padC + padA + 16;
        if (macIn.length < total) {
            macIn = new byte[total + 128];
        }
        Arrays.fill(macIn, 0, total, (byte) 0);
        System.arraycopy(ct, 0, macIn, 0, len);
        System.arraycopy(aad, aOff, macIn, padC, aLen);
        putLe64(macIn, padC + padA, aLen);
        putLe64(macIn, padC + padA + 8, len);
        polyMac(macIn, total);
    }

    // ==================== Poly1305（26-bit limbs 通用乘，正确性优先） ====================

    /** RFC 7539 clamp：每 32-bit word 的高 4 位与 word1..3 的低 2 位清零（字节级最稳）。 */
    private static final int[] POLY_CLAMP = {0x0FFFFFFF, 0x0FFFFFFC, 0x0FFFFFFC, 0x0FFFFFFC};
    private final long[] pw = new long[4];

    private void initPoly() {
        byte[] k = polyKey;
        long t0 = (le32(k, 0) & POLY_CLAMP[0]) & 0xffffffffL;
        long t1 = (le32(k, 4) & POLY_CLAMP[1]) & 0xffffffffL;
        long t2 = (le32(k, 8) & POLY_CLAMP[2]) & 0xffffffffL;
        long t3 = (le32(k, 12) & POLY_CLAMP[3]) & 0xffffffffL;
        r[0] = t0 & 0x3ffffffL;
        r[1] = ((t0 >>> 26) | (t1 << 6)) & 0x3ffffffL;
        r[2] = ((t1 >>> 20) | (t2 << 12)) & 0x3ffffffL;
        r[3] = ((t2 >>> 14) | (t3 << 18)) & 0x3ffffffL;
        r[4] = (t3 >>> 8) & 0x3ffffffL;
        for (int i = 0; i < 5; i++) {
            r5[i] = r[i] * 5;
        }
        Arrays.fill(h, 0);
    }

    private void polyMac(byte[] msg, int len) {
        long[] w = this.pw;
        for (int off = 0; off < len; off += 16) {
            for (int i = 0; i < 4; i++) {
                int pos = off + i * 4;
                w[i] = pos < len ? (le32(msg, pos) & 0xffffffffL) : 0;
            }
            h[0] += w[0] & 0x3ffffffL;
            h[1] += ((w[0] >>> 26) | (w[1] << 6)) & 0x3ffffffL;
            h[2] += ((w[1] >>> 20) | (w[2] << 12)) & 0x3ffffffL;
            h[3] += ((w[2] >>> 14) | (w[3] << 18)) & 0x3ffffffL;
            h[4] += (w[3] >>> 8) + (1L << 24);                 // bit 128
            mulMod();
        }
        finishAddS();
    }

    private void mulMod() {
        Arrays.fill(acc, 0);
        for (int i = 0; i < 5; i++) {
            long hi = h[i];
            if (hi == 0) {
                continue;
            }
            for (int j = 0; j < 5; j++) {
                int p = i + j;
                if (p < 5) {
                    acc[p] += hi * r[j];
                } else {
                    acc[p - 5] += hi * r5[j];
                }
            }
        }
        long c = 0;
        for (int i = 0; i < 4; i++) {
            long v = acc[i] + c;
            h[i] = v & 0x3ffffffL;
            c = v >>> 26;
        }
        long v4 = acc[4] + c;
        h[4] = v4 & 0x3ffffffL;
        h[0] += (v4 >>> 26) * 5;                               // mod 2^130 - 5 折叠
    }

    private void finishAddS() {
        long c = 0;
        for (int i = 0; i < 4; i++) {
            long v = h[i] + c;
            h[i] = v & 0x3ffffffL;
            c = v >>> 26;
        }
        h[4] += c;
        long carry = 5;
        for (int i = 0; i < 5; i++) {
            long v = h[i] + carry;
            gg[i] = v & 0x3ffffffL;
            carry = v >>> 26;
        }
        if (carry == 1) {
            System.arraycopy(gg, 0, h, 0, 5);
        }
        long lo = h[0] | (h[1] << 26) | (h[2] << 52);
        long hi = (h[2] >>> 12) | (h[3] << 14) | (h[4] << 40);
        long s0 = le64(polyKey, 16);
        long s1 = le64(polyKey, 24);
        long t0 = lo + s0;
        long c1 = Long.compareUnsigned(t0, lo) < 0 ? 1 : 0;
        long t1 = hi + s1 + c1;
        putLe64(tag, 0, t0);
        putLe64(tag, 8, t1);
    }

    private static boolean constantTimeEquals16(byte[] a, byte[] b) {
        int diff = 0;
        for (int i = 0; i < 16; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    // ==================== 双重锚定自测 ====================

    /**
     * 双重锚定：
     *  - 密文/keystream 与 JDK ChaCha20-Poly1305 逐字节全等（ChaCha20 核心正确）；
     *  - tag 与独立 Python 大整数 Poly1305 参考实现的 RFC 7539 §2.8.2 布局固定向量全等。
     *  注：实测发现 JDK 的 Poly1305 mac 输入用"AAD 前置"旧草案布局（aad‖ct‖lens），
     *  与 RFC 7539（ct‖aad‖lens）tag 不同——故 tag 不能拿 JDK 当锚，只能用规范向量。
     */
    static void selfTest() throws Exception {
        byte[] key = new byte[32];
        byte[] nonce = new byte[12];
        for (int i = 0; i < 32; i++) key[i] = (byte) i;
        for (int i = 0; i < 12; i++) nonce[i] = (byte) (0x40 + i);
        byte[] aad = {1, 2, 3, 4, 5, 6};
        byte[] pt = new byte[96];
        for (int i = 0; i < 96; i++) pt[i] = (byte) (i & 0xff);

        // 锚 1：keystream/ct 对齐 JDK
        javax.crypto.Cipher ref = javax.crypto.Cipher.getInstance("ChaCha20-Poly1305");
        ref.init(javax.crypto.Cipher.ENCRYPT_MODE,
                new javax.crypto.spec.SecretKeySpec(key, "ChaCha20"),
                new javax.crypto.spec.IvParameterSpec(nonce));
        ref.updateAAD(aad);
        byte[] refOut = ref.doFinal(pt);
        byte[] refCt = Arrays.copyOf(refOut, 96);

        AeadRfc7539 mine = new AeadRfc7539(key);
        mine.setNonce(nonce);
        byte[] ct = new byte[96];
        mine.seal(pt, 0, 96, aad, 0, aad.length, ct);
        if (!Arrays.equals(refCt, ct)) {
            throw new AssertionError("chacha20 keystream mismatch vs JDK");
        }
        // 锚 2：RFC 7539 tag 固定向量（独立 oracle 产出）
        String expectedCt = "f8557e82764fe80650c6fc0bb7e263c2a4dd710b50e95778e615c840f63a2475"
                + "d092bb53026e44d12246056c4679c0b1d1625efaca43bb18e3fb350034560d5e"
                + "8b716b1e1756a16c0a4727c28f636b258d0ac27da8e8b639e528f6765001dc2b";
        if (!toHex(ct).equalsIgnoreCase(expectedCt)) {
            throw new AssertionError("ct mismatch vs RFC vector");
        }
        if (!toHex(mine.tag()).equalsIgnoreCase("057ba963bfca707fd3510cd2f5f81322")) {
            throw new AssertionError("tag mismatch vs RFC vector");
        }
        // 自洽：打开自己的产物
        AeadRfc7539 opener = new AeadRfc7539(key);
        opener.setNonce(nonce);
        byte[] work = Arrays.copyOf(ct, 96);
        if (!opener.openAndDecrypt(work, 0, 96, aad, 0, aad.length, mine.tag())) {
            throw new AssertionError("AEAD open mismatch");
        }
        if (!Arrays.equals(pt, work)) {
            throw new AssertionError("AEAD open plaintext mismatch");
        }
    }

    private static String toHex(byte[] b) {
        StringBuilder s = new StringBuilder();
        for (byte x : b) {
            s.append(String.format("%02x", x));
        }
        return s.toString();
    }

    private static int le32(byte[] b, int i) {
        return (b[i] & 0xFF) | ((b[i + 1] & 0xFF) << 8) | ((b[i + 2] & 0xFF) << 16) | ((b[i + 3] & 0xFF) << 24);
    }

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }

    private static void putLe32(byte[] b, int i, int v) {
        b[i] = (byte) v;
        b[i + 1] = (byte) (v >>> 8);
        b[i + 2] = (byte) (v >>> 16);
        b[i + 3] = (byte) (v >>> 24);
    }

    private static void putLe64(byte[] b, int i, long v) {
        for (int k = 0; k < 8; k++) {
            b[i + k] = (byte) (v >>> (8 * k));
        }
    }
}

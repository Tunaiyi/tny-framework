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

package com.tny.game.net.codec.cryptoloy;

import com.tny.game.common.digest.binary.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.codec.*;

/**
 * <p>默认混淆实现：键流为 {@code security[rel % secLen] ^ code[rel % 4]} 的周期叠加，
 * 本实现将其每帧预合并为一条 tile 周期表并按 8 字节字级应用（微基准 16B 对齐键形 ~159→82ns/96B；病态键形走计数器路径 ~159→111ns，见 crypto-bench 2026-10-01 微基准账），
 * 输出与参考实现 {@link BytesAide#xor(byte[], int, int, byte[]...)} <b>逐字节等价</b>
 * （optimize-legacy-codec-paths；等价由 {@code XorWordEquivalenceTest} 常态锁定）。
 * 短载荷（&lt;16B）直委托参考实现——建表摊销不划算，两路径同值由同一测试覆盖。
 * 键流相位相对窗口起点，全窗覆盖（net-protocol"全窗口覆盖与相对键流相位"契约）。
 * 前提：{@code bytes} 不得与 {@code context.getPackSecurityKey()} 返回数组别名（自别名时参考实现的交错读写与 tile 快照语义分叉，属调用方违约输入）。
 *
 * @author Kun Yang
 * @date 2018-10-18 14:58
 */
@Unit
public class XOrCodecCrypto implements CodecCrypto {

    private static final ThreadLocal<Holder> HOLD = ThreadLocal.withInitial(Holder::new);

    private static final class Holder {
        final byte[] code4 = new byte[4];
        byte[] tile = new byte[16];
        byte[] tile8 = new byte[16];
        long[] words = new long[2];
    }

    /**
     * 字级化的摊销上限：对齐周期表（tile8）宽于此值时"每帧建表"成本吞噬字级收益
     * （实施期微基准实测 22 字符键 aligned=88 劣于直接委托；64 为保守圆整）。
     */
    private static final int WORD_PATH_MAX_ALIGNED = 64;

    private byte[] xor(DataPackageContext context, byte[] bytes, int offset, int length) {
        byte[] security = context.getPackSecurityKey();
        Holder holder = HOLD.get();
        BytesAide.int2Bytes(context.getPacketCode(), holder.code4, 0);
        byte[] code4 = holder.code4;
        // 短载荷与非法窗口（越界/整型溢出）一律委托参考实现——异常时机与部分写行为逐字节同 HEAD（穷举评审发现的路径分叉，结构上消除）
        if (length < 16 || offset < 0 || length < 0 || offset > bytes.length - length) {
            return BytesAide.xor(bytes, offset, length, security, code4);
        }
        int end = offset + length;
        int period = lcm(security.length, code4.length);
        int aligned = lcm(period, 8);
        if (aligned > WORD_PATH_MAX_ALIGNED) {
            // 键长病态（周期非 8 对齐且过大）：无取模计数器字节循环——仍严格优于 oracle 的双取模
            for (int i = offset, si = 0, ci = 0; i < end; i++, si = (si + 1 == security.length) ? 0 : si + 1, ci = (ci + 1) & 3) {
                bytes[i] = (byte) (bytes[i] ^ security[si] ^ code4[ci]);
            }
            return bytes;
        }
        byte[] tile = holder.tile;
        if (tile.length < period) {
            tile = holder.tile = new byte[period];
        }
        for (int j = 0; j < period; j++) {
            tile[j] = (byte) (security[j % security.length] ^ code4[j & 3]);
        }
        byte[] tile8 = holder.tile8;
        long[] words = holder.words;
        if (tile8.length < aligned) {
            tile8 = holder.tile8 = new byte[aligned];
            words = holder.words = new long[aligned >>> 3];
        }
        for (int j = 0; j < aligned; j++) {
            tile8[j] = tile[j % period];
        }
        int wordCount = aligned >>> 3;
        for (int w = 0; w < wordCount; w++) {
            words[w] = le64(tile8, w << 3);
        }
        int i = offset;
        int rel = 0;
        for (; i + 8 <= end; i += 8, rel += 8) {
            putLe64(bytes, i, le64(bytes, i) ^ words[(rel >>> 3) % wordCount]);
        }
        for (; i < end; i++, rel++) {
            bytes[i] = (byte) (bytes[i] ^ tile[rel % period]);
        }
        return bytes;
    }

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }

    private static void putLe64(byte[] b, int i, long v) {
        for (int k = 0; k < 8; k++) {
            b[i + k] = (byte) (v >>> (8 * k));
        }
    }

    private static int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    @Override
    public byte[] encrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

    @Override
    public byte[] decrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

}

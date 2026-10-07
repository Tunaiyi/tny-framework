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
 * 混淆键流的 tile 预合并实现（add-mac-generation-siphash）：与 {@link XOrCodecCrypto}
 * 输出<b>逐字节等价</b>（等价性由测试随机窗口×多键长断言钉死），仅实现形态不同——
 * 每帧一次 O(period) 建表（period = lcm(securityLen, 4)），主循环消灭每字节双取模，
 * 以增量索引推进相位；tile 缓冲线程封闭复用，热路径零分配（设计决策 4）。
 *
 * <p>语义定位与 XOr 一致：混淆/抗分析层，非密码学加密；密钥材料同源
 * （{@code packager.getPackSecurityKey()} 每包轮换 + packetCode 扩掩）。
 */
@Unit
public class XorTileCodecCrypto implements CodecCrypto {

    private static final ThreadLocal<TileHolder> TILE = ThreadLocal.withInitial(TileHolder::new);

    private static final class TileHolder {
        byte[] tile = new byte[64];
        final byte[] code4 = new byte[4];
    }

    @Override
    public byte[] encrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

    @Override
    public byte[] decrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

    private byte[] xor(DataPackageContext context, byte[] bytes, int offset, int length) {
        byte[] security = context.getPackSecurityKey();
        TileHolder holder = TILE.get();
        BytesAide.int2Bytes(context.getPacketCode(), holder.code4, 0);
        byte[] code = holder.code4;
        // 非法窗口（越界/整型溢出）委托参考实现：与 XOr/oracle 在全输入域同构（异常时机与部分写逐比特一致）
        if (offset < 0 || length < 0 || offset > bytes.length - length) {
            return BytesAide.xor(bytes, offset, length, security, code);
        }
        int period = lcm(security.length, code.length);
        byte[] tile = holder.tile;
        if (tile.length < period) {
            tile = new byte[period];
            holder.tile = tile;
        }
        for (int j = 0; j < period; j++) {
            tile[j] = (byte) (security[j % security.length] ^ code[j % code.length]);
        }
        int end = offset + length;
        for (int i = offset, n = 0; i < end; i++, n = (n + 1 == period) ? 0 : n + 1) {
            bytes[i] = (byte) (bytes[i] ^ tile[n]);
        }
        return bytes;
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
}

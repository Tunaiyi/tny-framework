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
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.codec.*;

import java.util.zip.CRC32;

/**
 * 快速筛选代次校验器（add-crc32-verify-tier）：IEEE CRC32（JDK {@link CRC32}，
 * x86 SSE4.2 / 新 aarch64 硬件 intrinsic，实测 ~12ns/向零分配），校验码 4 字节——
 * 帧尾较 8B 档省 4 字节。
 *
 * <p><b>能力边界（必读）：本类不提供任何防伪能力。</b>校验码为无密钥可重算的线性函数，
 * 混入 accessKey/number/code 仅延续既有 keyed-CRC 的"抬高伪造门槛"语义
 * （与 {@link CRC64CodecVerifier} 同级、与 {@link SipHash24CodecVerifier} 的键控 MAC 不同级）。
 * 对外玩家链路应选认证代次；本档适用面 = 内网/高扇出/可信任对端的快速一致性过滤。
 * 详见 {@code codec/security-generations_readme.md}。
 *
 * <p>混入序列与既有代次完全同构（设计决策 3）：{@code number4 ‖ body窗口 ‖ accessKey ‖ code4}，
 * 流式 {@code update} 吸收窗口、不复制；实例经 {@code ThreadLocal} 线程封闭（P1 抽象层纯 JDK、
 * P13 热路径无锁零分配，先例 {@code SipHash24CodecVerifier}）。
 *
 * <p>启用即帧级 wire 变化（4B 尾）：链路两端必须同批切换，跨语言对表 .NET
 * {@code System.IO.Hashing.Crc32}（同 IEEE 多项式，零移植成本）。
 */
@Unit
public class Crc32CodecVerifier implements CodecVerifier {

    private static final ThreadLocal<Holder> HOLD = ThreadLocal.withInitial(Holder::new);

    private static final class Holder {
        final CRC32 crc = new CRC32();
        final byte[] num4 = new byte[4];
        final byte[] code4 = new byte[4];
    }

    @Override
    public int getCodeLength() {
        return 4;
    }

    @Override
    public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
        byte[] accessKey = packager.getAccessKeyBytes();
        Holder holder = HOLD.get();
        BytesAide.int2Bytes(packager.getPacketNumber(), holder.num4, 0);
        BytesAide.int2Bytes(packager.getPacketCode(), holder.code4, 0);
        CRC32 crc = holder.crc;
        crc.reset();
        crc.update(holder.num4, 0, 4);
        crc.update(body, offset, length);
        crc.update(accessKey, 0, accessKey.length);
        crc.update(holder.code4, 0, 4);
        return BytesAide.int2Bytes((int) crc.getValue());
    }

    @Override
    public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
        if (verifyCode == null || verifyCode.length != 4) {
            return false;
        }
        byte[] mine = generate(packager, body, offset, length);
        int diff = (mine[0] ^ verifyCode[0]) | (mine[1] ^ verifyCode[1])
                | (mine[2] ^ verifyCode[2]) | (mine[3] ^ verifyCode[3]);
        return diff == 0;
    }
}

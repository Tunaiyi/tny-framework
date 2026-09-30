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
package com.tny.game.net.codec.verifier;

import com.tny.game.common.digest.*;
import com.tny.game.common.digest.binary.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.codec.*;

/**
 * 键控认证代次校验器（add-mac-generation-siphash）：SipHash-2-4 截断 8 字节——
 * 与 {@link CRC64CodecVerifier} 同为 {@link CodecVerifier} SPI 的兄弟实现，按
 * {@code NetPacketCodecSetting.verifier} unit 名装配切换，帧布局与校验码长度完全一致。
 *
 * <p>混入序列与生产 CRC64 完全同构（设计决策 1）：{@code number ‖ body窗口 ‖ accessKey ‖ code}，
 * 包号绑定使"旧载荷换新包号"的换皮重放必须失败；密钥取连接 accessKey 派生前 16 字节
 * （R2 会话代次仅替换密钥来源，本类结构不动）。
 *
 * <p>verifier 实例全连接共享（单一 codec 注入 pipeline），累加器与输入缓冲经
 * {@link ThreadLocal} 线程封闭——热路径零分配、无锁（P1：抽象层保持纯 JDK 依赖；
 * netty4 装配层如需 FastThreadLocal 化可另立优化件）。
 */
@Unit
public class SipHash24CodecVerifier implements CodecVerifier {

    private static final ThreadLocal<Holder> HOLD = ThreadLocal.withInitial(Holder::new);

    /** 每线程复用的 MAC 累加器与 number/code 输入窗 */
    private static final class Holder {
        final byte[] num4 = new byte[4];
        final byte[] code4 = new byte[4];
        final SipHash24 mac = new SipHash24(0L, 0L);
    }

    @Override
    public int getCodeLength() {
        return 8;
    }

    @Override
    public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
        byte[] accessKey = packager.getAccessKeyBytes();
        Holder holder = HOLD.get();
        BytesAide.int2Bytes(packager.getPacketNumber(), holder.num4, 0);
        BytesAide.int2Bytes(packager.getPacketCode(), holder.code4, 0);
        SipHash24 mac = holder.mac;
        mac.reset(BytesAide.bytes2Long(accessKey, 0), BytesAide.bytes2Long(accessKey, 8));
        mac.update(holder.num4, 0, 4);
        mac.update(body, offset, length);
        mac.update(accessKey, 0, accessKey.length);
        mac.update(holder.code4, 0, 4);
        return BytesAide.long2Bytes(mac.digest());
    }

    @Override
    public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
        if (verifyCode == null || verifyCode.length != 8) {
            return false;
        }
        byte[] mine = generate(packager, body, offset, length);
        int diff = 0;
        for (int i = 0; i < 8; i++) {
            diff |= mine[i] ^ verifyCode[i];
        }
        return diff == 0;
    }
}

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

package com.tny.game.net.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018 -10-16 21:06
 */
@UnitInterface
public interface CodecCrypto {

    /**
     * 对 {@code bytes} 的窗口 {@code [offset, offset+length)} 全体字节施加键流变换（原地修改并返回同一数组）。
     * 实现契约（net-protocol / fix-xor-crypto-scope）：
     * <ul>
     * <li>全窗处理——窗口内每一个字节都必须被变换，MUST NOT 依赖或泄露缓冲在内存池中的绝对位置；</li>
     * <li>键流相位 MUST 以窗口内相对位置（{@code i - offset}）计算，
     * 使同一消息在任何窗口起点下产生逐字节相同的线上结果；</li>
     * <li>自逆——{@code decrypt} 对 {@code encrypt} 的产物以相同 packager 状态调用必须还原原文（XOR 类流变换天然满足）；</li>
     * <li>两端一致——{@code packager} 的包号/包码由编解码框架保证收发同步，实现只可消费该状态。</li>
     * </ul>
     */
    byte[] encrypt(DataPackageContext packager, byte[] bytes, int offset, int length);

    /**
     * {@link #encrypt} 的逆变换：同一窗口契约与相对相位规则，参数语义完全一致。
     */
    byte[] decrypt(DataPackageContext packager, byte[] bytes, int offset, int length);

}

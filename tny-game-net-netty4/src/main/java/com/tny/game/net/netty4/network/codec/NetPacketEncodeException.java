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
package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;

/**
 * 报文编码方向异常（fix-packet-verifier-gate D2 修正版）：
 * 与解码方向异常区分——编码失败仅使本次写回执失败，不关闭通道。
 * 继承 NetException 而非 NetCodecException：后两者构造器为 private（final 语义），
 * 打开 net 抽象模块公共面违背 P1/P11，故实现模块自建类型。
 */
public class NetPacketEncodeException extends NetException {

    public NetPacketEncodeException(String message, Object... args) {
        super(NetResultCode.ENCODE_ERROR, message, args);
    }

}

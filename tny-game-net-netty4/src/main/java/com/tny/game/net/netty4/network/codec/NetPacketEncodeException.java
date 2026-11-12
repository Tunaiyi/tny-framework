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

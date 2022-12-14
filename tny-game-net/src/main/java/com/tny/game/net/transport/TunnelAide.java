/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport;

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-16 15:55
 */
public class TunnelAide {

    public static final Logger LOGGER = LoggerFactory.getLogger(TunnelAide.class);

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param request 响应请求
     * @param code    消息码
     * @param body    消息体
     */
    public static <UID> void responseMessage(NetTunnel<UID> tunnel, Message request, ResultCode code, Object body) {
        MessageContext context = MessageContexts.respond(request, code, body, request.getId());
        responseMessage(tunnel, code, context);
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param context 消息信息上下文
     */
    public static <UID> SendReceipt responseMessage(NetTunnel<UID> tunnel, ResultCode code, MessageContext context) {
        boolean close = code.getLevel() == ResultLevel.ERROR;
        if (close) {
            if (!context.isWriteAwaitable()) {
                context.willWriteAwaiter();
            }
        }
        SendReceipt receipt = tunnel.send(context);
        if (close) {
            receipt.written().thenRun(tunnel::close);
        }
        return receipt;
    }

}

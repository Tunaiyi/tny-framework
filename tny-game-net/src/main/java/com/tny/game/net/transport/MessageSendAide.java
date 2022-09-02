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
public class MessageSendAide {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageSendAide.class);

    private static RpcForwardHeader createBackForwardHeader(RpcForwardHeader messageForwardHeader) {
        if (messageForwardHeader != null) {
            return RpcForwardHeaderBuilder.newBuilder()
                    .setFrom(messageForwardHeader.getTo())
                    .setSender(messageForwardHeader.getReceiver())
                    .setTo(messageForwardHeader.getFrom())
                    .setReceiver(messageForwardHeader.getSender())
                    .build();
        }
        return null;
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param request 响应请求
     * @param code    消息码
     * @param body    消息体
     * @return 发送回执
     */
    public static <UID> SendReceipt response(NetTunnel<UID> tunnel, Message request, ResultCode code, Object body) {
        var idHeader = request.getHeader(MessageHeaderConstants.RPC_ORIGINAL_MESSAGE_ID);
        var toMessage = request.getId();
        if (idHeader != null) {
            toMessage = idHeader.getMessageId();
        }
        var forwardHeader = request.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        var backForward = createBackForwardHeader(forwardHeader);
        MessageContext context = MessageContexts.respond(request, code, body, toMessage)
                .withHeader(backForward);
        return send(tunnel, context, backForward == null);
    }

    public static <UID> SendReceipt push(NetTunnel<UID> tunnel, Message request, ResultCode code, Object body) {
        var messageForwardHeader = request.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        var backForward = createBackForwardHeader(messageForwardHeader);
        var context = MessageContexts.push(request, code, body)
                .withHeader(backForward);
        return send(tunnel, context, backForward == null);
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param context 消息信息上下文
     */
    public static <UID> SendReceipt send(NetTunnel<UID> tunnel, MessageContext context) {
        return send(tunnel, context, true);
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param context 消息信息上下文
     */
    public static <UID> SendReceipt send(NetTunnel<UID> tunnel, MessageContext context, boolean autoClose) {
        boolean close = context.getResultCode().getLevel() == ResultLevel.ERROR;
        if (close) {
            if (!context.isWriteAwaitable()) {
                context.willWriteAwaiter();
            }
        }
        SendReceipt receipt = tunnel.send(context);
        if (autoClose && close) {
            receipt.written().thenRun(tunnel::close);
        }
        return receipt;
    }

}

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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-16 15:55
 */
public class RpcMessageAide {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcMessageAide.class);

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param content 消息信息上下文
     */
    public static  SendReceipt send(NetTunnel tunnel, MessageContent content) {
        return send(tunnel, content, !content.existHeader(MessageHeaderConstants.RPC_FORWARD_HEADER));
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param content 消息信息上下文
     */
    public static  SendReceipt send(NetTunnel tunnel, MessageContent content, boolean autoClose) {
        boolean close = content.getResultCode().getLevel() == ResultLevel.ERROR;
        if (close) {
            if (!content.isWriteAwaitable()) {
                content.willWriteFuture();
            }
        }
        SendReceipt receipt = tunnel.send(content);
        if (autoClose && close) {
            receipt.written().thenRun(tunnel::close);
        }
        return receipt;
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param context rpc上下文
     * @param code    消息码
     * @param body    消息体
     * @return 发送回执
     */
    static MessageContent toMessage(RpcEnterContext context, ResultCode code, Object body) {
        var request = context.getMessage();
        if (request.getMode() == MessageMode.REQUEST) {
            return respond(request, code, body);
        } else {
            return push(request, code, body);
        }
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param request 请求
     * @param code    消息码
     * @param body    消息体
     * @return 发送回执
     */
    static MessageContent toMessage(Message request, ResultCode code, Object body) {
        if (request.getMode() == MessageMode.REQUEST) {
            return respond(request, code, body);
        } else {
            return push(request, code, body);
        }
    }

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

    private static MessageContent putTransitiveHeaders(Message request, MessageContent content) {
        var headers = request.getAllHeaders();
        if (headers == null) {
            return content;
        }
        for (MessageHeader<?> header : headers) {
            if (header.isTransitive()) {
                content.withHeader(header);
            }
        }
        return content;
    }

    private static MessageContent push(Message request, ResultCode code, Object body) {
        var messageForwardHeader = request.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        var backForward = createBackForwardHeader(messageForwardHeader);
        return putTransitiveHeaders(request, MessageContents.push(request, code, body))
                .withHeader(backForward);
    }

    private static MessageContent respond(Message request, ResultCode code, Object body) {
        var forwardHeader = request.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        var backForward = createBackForwardHeader(forwardHeader);
        return putTransitiveHeaders(request, MessageContents.respond(request, code, body, request.getOriginalId()))
                .withHeader(backForward);
    }

    public static void ignoreHeaders(NetMessage message, Collection<String> ignoreSet) {
        if (CollectionUtils.isNotEmpty(ignoreSet)) {
            if (ignoreSet.contains("*")) {
                message.removeAllHeaders();
            } else {
                message.removeHeaders(ignoreSet);
            }
        }
    }

}

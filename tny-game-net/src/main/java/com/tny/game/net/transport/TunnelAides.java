package com.tny.game.net.transport;

import com.tny.game.common.result.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-16 15:55
 */
public class TunnelAides {

    public static final Logger LOGGER = LoggerFactory.getLogger(TunnelAides.class);


    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param request 响应请求
     * @param code    消息码
     * @param body    消息体
     */
    public static <UID> void responseMessage(NetTunnel<UID> tunnel, Message<UID> request, ResultCode code, Object body) {
        MessageContext<UID> context = MessageContexts
                .respond(request, code, body, request.getId());
        responseMessage(tunnel, request, context);
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param request 响应请求
     * @param context 消息信息上下文
     */
    public static <UID> void responseMessage(NetTunnel<UID> tunnel, Message<UID> request, MessageContext<UID> context) {
        ResultCode code = context.getResultCode();
        // TODO 是否要处理Tail?
        // if (request.existTail())
        //     context.setTail(request.getTail(Object.class));
        SendContext<UID> sendContext = tunnel.send(context);
        if (code.getType() == ResultCodeType.ERROR) {
            sendContext.getWriteFuture().addWriteListener(future -> tunnel.close());
        }
    }


}

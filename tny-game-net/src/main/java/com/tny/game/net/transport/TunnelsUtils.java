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
public class TunnelsUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(TunnelsUtils.class);


    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param request 响应请求
     * @param code    消息码
     * @param body    消息体
     */
    public static <UID> void responseMessage(Tunnel<UID> tunnel, Message<UID> request, ResultCode code, Object body) {
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
    public static <UID> void responseMessage(Tunnel<UID> tunnel, Message<UID> request, MessageContext<UID> context) {
        MessageHeader header = request.getHeader();
        ResultCode code = context.getCode();
        if (header.isHasAttachment())
            context.setAttachment(header.getAttachment(Object.class));
        if (code.getType() == ResultCodeType.ERROR) {
            context = context
                    .willSendFuture(future -> future.whenComplete((msg, e) -> tunnel.close()));
        }
        tunnel.sendAsyn(context);
    }


}

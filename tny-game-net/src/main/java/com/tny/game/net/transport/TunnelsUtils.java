package com.tny.game.net.transport;

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-16 15:55
 */
public class TunnelsUtils {

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param request 响应请求
     * @param code    消息码
     * @param body    消息体
     */
    public static <UID> void responseMessage(Tunnel<UID> tunnel, Message<UID> request, ResultCode code, Object body) {
        MessageSubject subject = MessageSubjectBuilder
                .respondBuilder(code, request.getHeader())
                .setBody(body)
                .build();
        responseMessage(tunnel, request, subject);
    }

    /**
     * 发送响应消息, 如果 code 为 Error, 则发送完后断开连接
     *
     * @param tunnel  通道
     * @param request 响应请求
     * @param subject 消息主体
     */
    public static <UID> void responseMessage(Tunnel<UID> tunnel, Message<UID> request, MessageSubject subject) {
        MessageHeader header = request.getHeader();
        ResultCode code = subject.getCode();
        MessageContext<UID> context = null;
        if (code.getType() == ResultCodeType.ERROR) {
            context = MessageContexts.<UID>createContext()
                    .willSendFuture(future -> future.whenComplete((msg, e) -> tunnel.close()));
        }
        if (header.isHasAttachment()) {
            context = ifNullAndGet(context, MessageContexts::createContext);
            tunnel.sendAsyn(subject, context.setAttachment(header.getAttachment(Object.class)));
        } else {
            tunnel.sendAsyn(subject, context);
        }
    }


}

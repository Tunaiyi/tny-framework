package com.tny.game.net.transport;

import com.tny.game.net.exception.NetException;
import com.tny.game.net.transport.message.MessageSubject;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface Communicator<UID> {

    /**
     * @return 用户ID
     */
    UID getUserId();

    /**
     * @return 用户组
     */
    String getUserType();

    /**
     * @return 登陆凭证
     */
    Certificate<UID> getCertificate();

    /**
     * @return 是否登陆认证
     */
    boolean isLogin();

    /**
     * @return 是否关闭终端
     */
    boolean isClosed();

    /**
     * 关闭终端
     */
    void close();

    /**
     * 异步发送消息
     *
     * @param subject 消息主体
     * @return 返回发送上下文
     */
    default void sendAsyn(MessageSubject subject) {
        this.sendAsyn(subject, null);
    }

    /**
     * 异步发送消息
     *
     * @param subject        消息主体
     * @param messageContext 发送消息上下文
     * @return 返回发送上下文
     */
    SendContext<UID> sendAsyn(MessageSubject subject, MessageContext<UID> messageContext);

    /**
     * 同步发送消息
     *
     * @param subject 消息主体
     * @param timeout 发送超时
     * @return 返回发送上下文
     */
    default SendContext<UID> sendSync(MessageSubject subject, long timeout) throws NetException {
        return sendSync(subject, null, timeout);
    }

    /**
     * 同步发送消息
     *
     * @param subject        消息主体
     * @param messageContext 发送消息上下文
     * @param timeout        发送超时
     * @return 返回发送上下文
     */
    SendContext<UID> sendSync(MessageSubject subject, MessageContext<UID> messageContext, long timeout) throws NetException;


}

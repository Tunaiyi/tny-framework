package com.tny.game.net.transport;

import com.tny.game.net.exception.*;
import com.tny.game.net.transport.message.*;

import java.util.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel<UID> extends Tunnel<UID> {

    /**
     * ping
     */
    void ping();

    /**
     * pong
     */
    void pong();

    /**
     * 发送消息
     *
     * @param content 消息内容
     */
    void send(MessageContext<UID> content) throws NetException;

    /**
     * 重新发送消息
     */
    void resend(ResendMessage<UID> message) throws NetException;

    /**
     * 接收消息
     *
     * @param message 消息
     */
    void receive(Message<UID> message);

    /**
     * 写出消息
     *
     * @param message  消息
     * @param callback 写出Future 对象
     * @throws TunnelWriteException 抛出异常
     */
    void write(Message<UID> message, WriteCallback<UID> callback) throws TunnelWriteException;

    /**
     * 写出消息
     *
     * @param message 消息
     * @throws TunnelWriteException 抛出异常
     */
    default void write(Message<UID> message) throws TunnelWriteException {
        write(message, null);
    }

    /**
     * 认证
     *
     * @param certificate 认证的凭证
     * @throws ValidatorFailException 接受凭证失败
     */
    void authenticate(Certificate<UID> certificate) throws ValidatorFailException;

    /**
     * @return 获取 MessageEventsBox
     */
    MessageEventsBox<UID> getEventsBox();

    /**
     * 消息工厂
     *
     * @return
     */
    MessageBuilderFactory<UID> getMessageBuilderFactory();

    /**
     * 注册回调future对象
     *
     * @param messageId     消息 Id
     * @param respondFuture future 对象
     */
    void registerFuture(long messageId, RespondFuture<UID> respondFuture);

    /**
     * 绑定 Session
     *
     * @param session 会话
     * @return 返回是否绑定成功
     */
    boolean bind(NetSession<UID> session);

    /**
     * 获取绑定会话
     *
     * @return 获取邦定回话
     */
    Optional<NetSession<UID>> getSession();
}


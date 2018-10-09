package com.tny.game.net.transport;

import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.message.*;

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
     * 开启
     */
    void open() throws ValidatorFailException;

    /**
     * 是否活跃
     */
    boolean isActive();

    /**
     * 接收消息
     *
     * @param message 消息
     */
    void receive(NetMessage<UID> message);

    /**
     * 认证
     *
     * @param certificate 认证的凭证
     * @throws ValidatorFailException 接受凭证失败
     */
    void authenticate(Certificate<UID> certificate) throws ValidatorFailException;

    /**
     * 注册回调future对象
     *
     * @param messageId     消息 Id
     * @param respondFuture future 对象
     */
    void registerFuture(long messageId, RespondFuture<UID> respondFuture);

    /**
     * 回调消息的响应Future
     *
     * @param message 消息
     */
    void callbackFuture(Message<UID> message);

    /**
     * 绑定 Session
     *
     * @param session 会话
     * @return 返回是否绑定成功
     */
    boolean bind(NetSession<UID> session);

}


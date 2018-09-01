package com.tny.game.net.tunnel;

import com.tny.game.net.exception.TunnelWriteException;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;

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
     * 绑定 Session
     *
     * @param session 会话
     * @return 返回是否绑定成功
     */
    boolean bind(NetSession<UID> session);

    /**
     * 写出消息
     *
     * @param message    消息
     * @param callback 写出Future 对象
     * @throws Throwable 抛出异常
     */
    void write(Message<UID> message, WriteCallback<UID> callback) throws TunnelWriteException;

    /**
     * 写出消息
     *
     * @param message    消息
     * @throws Throwable 抛出异常
     */
    default void write(Message<UID> message) throws Exception {
        write(message, null);
    }

    @Override
    NetSession<UID> getSession();

    MessageBuilderFactory<UID> getMessageBuilderFactory();

}

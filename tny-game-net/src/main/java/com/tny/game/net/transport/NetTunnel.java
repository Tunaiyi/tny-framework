package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel<UID> extends Tunnel<UID>, Transport<UID>, Receiver<UID>, Sender<UID> {

    /**
     * @return 获取绑定中断
     */
    @Override
    NetEndpoint<UID> getEndpoint();

    /**
     * 设置访问 Id
     *
     * @param accessId 访问 Id
     */
    void setAccessId(long accessId);

    /**
     * ping
     */
    void ping();

    /**
     * pong
     */
    void pong();

    /**
     * 打开
     */
    boolean open();

    /**
     * 断开
     */
    void disconnect();


    /**
     * 终端 Endpoint
     *
     * @param endpoint 终端
     * @return 返回是否绑定成功
     */
    boolean bind(NetEndpoint<UID> endpoint);

    /**
     * @param timeout
     * @return 创建写出 Future
     */
    WriteMessagePromise createWritePromise(long timeout);

    /**
     * @return message factory
     */
    MessageFactory<UID> getMessageFactory();

}


package com.tny.game.net.endpoint;

import com.tny.game.net.endpoint.task.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public interface NetEndpoint<UID> extends Endpoint<UID>, MessageMaker<UID>, SentMessageHistory, Receiver<UID> {

    /**
     * 处理收到消息
     *
     * @param tunnel  通道
     * @param message 消息
     */
    boolean receive(NetTunnel<UID> tunnel, Message message);

    /**
     * 异步发送消息
     *
     * @param tunnel         发送的通道
     * @param messageContext 发送消息上下文
     * @return 返回发送上下文
     */
    SendContext<UID> send(NetTunnel<UID> tunnel, MessageContext<UID> messageContext);

    /**
     * 使用指定认证登陆
     *
     * @param tunnel 指定认证
     */
    void online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws ValidatorFailException;

    /**
     * 通道销毁
     *
     * @param tunnel 销毁通道
     */
    void onUnactivated(NetTunnel<UID> tunnel);

    /**
     * 创建消息
     *
     * @param tunnel  管道
     * @param context 消息内容
     */
    void writeMessage(NetTunnel<UID> tunnel, MessageContext<UID> context);

    /**
     * @return 获取RespondFuture管理器
     */
    RespondFutureHolder getRespondFutureHolder();

    /**
     * @return 消息盒子
     */
    CommandTaskBox getCommandTaskBox();

    /**
     * 载入消息盒子
     *
     * @param commandTaskBox 消息
     */
    void takeOver(CommandTaskBox commandTaskBox);

    /**
     * @return 获取EndpointContext上下文
     */
    EndpointContext<UID> getContext();

}

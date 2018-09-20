package com.tny.game.net.transport;

import com.google.common.collect.Range;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.message.*;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID> {

    /**
     * @param message 添加发送消息
     */
    void addSentMessage(Message<UID> message);

    /**
     * 通过响应ID寻找已发送消息
     * ø
     *
     * @param messageId 响应ID
     * @return 返回消息
     */
    Message<UID> getSentMessage(long messageId);

    /**
     * 获取指定范围的已发送消息
     *
     * @param range 指定范围
     * @return 获取消息列表
     */
    List<Message<UID>> getSentMessages(Range<Long> range);

    /**
     * 使用指定认证登陆
     *
     * @param tunnel 指定认证
     */
    void acceptTunnel(NetTunnel<UID> tunnel) throws ValidatorFailException;

    /**
     * session下线, 不立即失效
     */
    void offline();

    /**
     * 使下线如果当前通道为指定通道
     *
     * @param tunnel 指定通道
     */
    boolean offlineIf(Tunnel<UID> tunnel);

    /**
     * 注册回调future对象
     *
     * @param messageId     消息 Id
     * @param respondFuture future 对象
     */
    void registerFuture(long messageId, RespondFuture<UID> respondFuture);

    /**
     * 获取指定消息的 future 对象
     *
     * @param messageId 消息 Id
     * @return 返回 future 对象
     */
    RespondFuture<UID> removeFuture(long messageId);

    /**
     * @return 创建消息 Id
     */
    long createMessageId();

}

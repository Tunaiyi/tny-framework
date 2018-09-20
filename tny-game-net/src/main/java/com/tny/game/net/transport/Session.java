package com.tny.game.net.transport;

import com.tny.game.common.context.Attributes;

/**
 * 用户会话对象 此对象从Socket链接便创建,保存用户链接后的属性对象,直到Socket断开连接
 *
 * @author KGTny
 */
public interface Session<UID> extends Communicator<UID> {

    /*
     * @return 会话ID
     */
    long getId();

    /**
     * @return 获取会话属性
     */
    Attributes attributes();

    /**
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isOnline();

    /**
     * 是否已下线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isOffline();

    /**
     * @return 获取下线时间
     */
    long getOfflineTime();

    /**
     * 发送消息
     *
     * @param content 消息内容
     */
    void send(MessageContext<UID> content);

    /**
     * 重发消息
     *
     * @param tunnelId 指定 tunnel 重发
     * @param message  重发消息
     */
    void resend(long tunnelId, ResendMessage<UID> message);

    /**
     * 重发消息
     *
     * @param message 重发消息
     */
    default void resend(ResendMessage<UID> message) {
        resend(-1, message);
    }
}

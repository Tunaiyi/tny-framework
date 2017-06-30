package com.tny.game.net.session;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.tunnel.Terminal;
import com.tny.game.net.tunnel.Tunnel;

import java.time.Instant;

/**
 * 用户会话对象 此对象从Socket链接便创建,保存用户链接后的属性对象,直到Socket断开连接
 *
 * @author KGTny
 */
public interface Session<UID> extends Terminal<UID> {

    /**
     * @return 会话ID
     */
    long getID();

    /**
     * @return 登陆时间
     */
    Instant getLoginAt();

    /**
     * @return 是否登錄
     */
    boolean isLogin();

    /**
     * @return获取会话属性
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
    // /**
    //  * @return 是否连接
    //  */
    // boolean isConnected();

    /**
     * @return 登陆凭证
     */
    LoginCertificate<UID> getCertificate();

    // /**
    //  * session下线
    //  *
    //  * @param invalid 是否立即失效
    //  */
    // void offline(boolean invalid);

    /**
     * session下线, 不立即失效
     */
    default void offline() {
        this.close();
    }

    /**
     * 使下线如果当前通道为指定通道
     *
     * @param tunnel 指定通道
     */
    void offlineIfCurrent(Tunnel<UID> tunnel);

    /**
     * @return 获取下线时间
     */
    long getOfflineTime();

    /**
     * @return 最后接受消息时间
     */
    long getLastReceiveTime();

    /**
     * 接收消息
     *
     * @param tunnel  消息通道
     * @param message 消息
     */
    void receive(Tunnel<UID> tunnel, Message<UID> message);

    /**
     * 发送消息
     *
     * @param tunnel  消息通道
     * @param content 消息内容
     */
    void send(Tunnel<UID> tunnel, MessageContent<?> content);

    /**
     * 重新messageID消息
     *
     * @param message 重发消息
     */
    void resend(Tunnel<UID> tunnel, ResendMessage message);

    /**
     * @return 获取当前通道
     */
    Tunnel<UID> getCurrentTunnel();

}

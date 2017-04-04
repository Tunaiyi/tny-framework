package com.tny.game.net.tunnel;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.session.ResendMessage;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface Terminal<UID> {

    /**
     * @return 用户ID
     */
    UID getUID();

    /**
     * @return 用户组
     */
    String getUserGroup();

    /**
     * 发送消息
     *
     * @param content 消息内容
     */
    void send(MessageContent<?> content);

    /**
     * 接收消息
     *
     * @param message 消息
     */
    void receive(Message<UID> message);

    /**
     * 重新messageID消息
     *
     * @param message 重发消息
     */
    void resend(ResendMessage message);

    /**
     * 关闭终端
     *
     * @return 是否关闭成功, 成功返回true 失效返回false
     */
    boolean close();

    /**
     * @return 是否关闭终端
     */
    boolean isClosed();

}

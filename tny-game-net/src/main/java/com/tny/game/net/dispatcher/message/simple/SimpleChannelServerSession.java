package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.ChannelServerSession;
import io.netty.channel.Channel;

/**
 * 游戏会话对象
 *
 * @author Kun.y
 */
public class SimpleChannelServerSession extends ChannelServerSession {

    public SimpleChannelServerSession(Channel channel) {
        super(channel);
    }

    public SimpleChannelServerSession(Channel channel, LoginCertificate loginInfo) {
        super(channel, loginInfo);
    }

}

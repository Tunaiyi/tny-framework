package com.tny.game.net.message.simple;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.ChannelServerSession;
import io.netty.channel.Channel;

/**
 * 游戏会话对象
 *
 * @author Kun.y
 */
public class SimpleChannelServerSession extends ChannelServerSession {

    private int number = 0;

    public SimpleChannelServerSession(Channel channel) {
        super(channel);
    }

    public SimpleChannelServerSession(Channel channel, LoginCertificate loginInfo) {
        super(channel, loginInfo);
    }

    @Override
    protected int createResponseNumber() {
        return number++;
    }

}

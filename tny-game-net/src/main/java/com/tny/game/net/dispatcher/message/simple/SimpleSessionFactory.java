package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.net.dispatcher.ServerSession;
import com.tny.game.net.dispatcher.ChannelServerSessionFactory;
import io.netty.channel.Channel;

public class SimpleSessionFactory implements ChannelServerSessionFactory {

    public SimpleSessionFactory() {
    }

    @Override
    public ServerSession createSession(Channel channel) {
        return new SimpleChannelServerSession(channel);
    }

}

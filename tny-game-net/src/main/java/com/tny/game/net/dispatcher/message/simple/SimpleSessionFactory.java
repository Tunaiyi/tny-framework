package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.net.dispatcher.ServerSession;
import com.tny.game.net.dispatcher.ServerSessionFactory;
import io.netty.channel.Channel;

public class SimpleSessionFactory implements ServerSessionFactory {

    public SimpleSessionFactory() {
    }

    @Override
    public ServerSession createSession(Channel channel) {
        return new SimpleChannelServerSession(channel);
    }

}

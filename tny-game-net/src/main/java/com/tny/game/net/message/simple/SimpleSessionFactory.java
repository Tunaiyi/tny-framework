package com.tny.game.net.message.simple;

import com.tny.game.net.dispatcher.NetServerSession;
import com.tny.game.net.dispatcher.ServerSession;
import com.tny.game.net.dispatcher.ChannelServerSessionFactory;
import io.netty.channel.Channel;

public class SimpleSessionFactory implements ChannelServerSessionFactory {

    public SimpleSessionFactory() {
    }

    @Override
    public NetServerSession createSession(Channel channel) {
        return new SimpleChannelServerSession(channel);
    }

}

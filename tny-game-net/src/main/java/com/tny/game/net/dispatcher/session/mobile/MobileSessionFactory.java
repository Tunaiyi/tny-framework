package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.net.dispatcher.ChannelServerSessionFactory;
import com.tny.game.net.dispatcher.NetServerSession;
import io.netty.channel.Channel;

public class MobileSessionFactory implements ChannelServerSessionFactory {

    public MobileSessionFactory() {
    }

    @Override
    public NetServerSession createSession(Channel channel) {
        return new MobileSession(channel);
    }

}
package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.net.dispatcher.ServerSession;
import com.tny.game.net.dispatcher.ChannelServerSessionFactory;
import io.netty.channel.Channel;

public class MobileSessionFactory implements ChannelServerSessionFactory {

    public MobileSessionFactory() {
    }

    @Override
    public ServerSession createSession(Channel channel) {
        return new MobileSession(channel);
    }

}
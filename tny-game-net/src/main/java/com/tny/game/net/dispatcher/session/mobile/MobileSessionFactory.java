package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.net.dispatcher.ServerSession;
import com.tny.game.net.dispatcher.ServerSessionFactory;
import io.netty.channel.Channel;

public class MobileSessionFactory implements ServerSessionFactory {

    public MobileSessionFactory() {
    }

    @Override
    public ServerSession createSession(Channel channel) {
        return new MobileSession(channel);
    }

}
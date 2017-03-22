package com.tny.game.del.mobile;

import com.tny.game.net.netty.NettySessionFactory;
import com.tny.game.net.dispatcher.NetServerSession;
import io.netty.channel.Channel;

public class MobileSessionFactory implements NettySessionFactory {

    public MobileSessionFactory() {
    }

    @Override
    public NetServerSession createSession(Channel channel) {
        return new MobileSession(channel);
    }

}
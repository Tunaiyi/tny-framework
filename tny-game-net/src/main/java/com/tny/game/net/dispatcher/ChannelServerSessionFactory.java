package com.tny.game.net.dispatcher;

import io.netty.channel.Channel;

public interface ChannelServerSessionFactory {

    ServerSession createSession(Channel channel);

}

package com.tny.game.net.dispatcher;

import io.netty.channel.Channel;

public interface ServerSessionFactory {

    public ServerSession createSession(Channel channel);

}

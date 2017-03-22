package com.tny.game.net.netty;

import com.tny.game.net.session.NetSession;
import io.netty.channel.Channel;

public interface NettySessionFactory<UID> {

    NetSession<UID> createSession(Channel channel);

}

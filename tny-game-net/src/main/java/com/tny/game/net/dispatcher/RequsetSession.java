package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;
import io.netty.channel.ChannelFuture;

public interface RequsetSession extends Session {

    public ChannelFuture request(Protocol protocol, Object... params);

    public <B> ChannelFuture request(Protocol protocol, MessageAction<B> action, Object... params);

    public <B> ChannelFuture request(Protocol protocol, MessageFuture<B> future, Object... params);

}
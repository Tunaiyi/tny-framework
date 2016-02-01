package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.DataPacketEncoder;
import io.netty.channel.ChannelFuture;

public interface ServerSession extends NetSession, ResponseSession {

    public DataPacketEncoder getEncoder();

    public RequestChecker getChecker();

    @Override
    public ChannelFuture response(Protocol protocol, Object body);

    @Override
    public ChannelFuture response(Protocol protocol, ResultCode code, Object body);

}
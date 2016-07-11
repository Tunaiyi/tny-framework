package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;
import io.netty.channel.ChannelFuture;

import java.util.Optional;

public interface ResponseSession extends Session {

    Optional<ChannelFuture> response(Protocol protocol, Object body);

    Optional<ChannelFuture> response(Protocol protocol, ResultCode code, Object body);

}
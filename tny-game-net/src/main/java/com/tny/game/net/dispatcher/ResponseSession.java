package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;
import io.netty.channel.ChannelFuture;

public interface ResponseSession extends Session {

    ChannelFuture response(Protocol protocol, Object body);

    ChannelFuture response(Protocol protocol, ResultCode code, Object body);

}
package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.session.Session;

import java.util.Optional;

public interface ResponseSession extends Session {

    default Optional<MessageSendFuture> response(Protocol protocol, Object body) {
        return this.response(protocol, ResultCode.SUCCESS, body);
    }

    Optional<MessageSendFuture> response(Protocol protocol, ResultCode code, Object body);

}
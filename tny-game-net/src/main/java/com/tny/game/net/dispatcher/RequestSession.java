package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;

import java.util.Optional;

public interface RequestSession extends Session {

    Optional<NetFuture> request(Protocol protocol, Object... params);

    Optional<NetFuture> request(Protocol protocol, MessageFuture<?> future, Object... params);

    default Optional<NetFuture> request(Protocol protocol, MessageAction<?> action, Object... params) {
        return request(protocol, action, 30000, params);
    }

    Optional<NetFuture> request(Protocol protocol, MessageAction<?> action, long timeout, Object... params);

}
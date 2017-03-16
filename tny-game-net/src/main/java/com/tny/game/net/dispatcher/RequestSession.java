package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;
import com.tny.game.net.session.Session;

import java.util.Optional;

public interface RequestSession extends Session {

    Optional<MessageSendFuture> request(Protocol protocol, Object... params);

    Optional<MessageSendFuture> request(Protocol protocol, MessageFuture<?> future, Object... params);

    default Optional<MessageSendFuture> request(Protocol protocol, MessageAction<?> action, Object... params) {
        return request(protocol, action, 30000, params);
    }

    Optional<MessageSendFuture> request(Protocol protocol, MessageAction<?> action, long timeout, Object... params);

}
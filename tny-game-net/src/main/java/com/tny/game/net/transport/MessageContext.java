package com.tny.game.net.transport;

import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.net.transport.message.*;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageContext<UID> extends SendContext<UID> {

    MessageContext<UID> willSendFuture(Consumer<MessageSendFuture<UID>> consumer);

    MessageContext<UID> willSendFuture();

    default MessageContext<UID> willResponseFuture(Consumer<StageableFuture<Message<UID>>> consumer) {
        return willResponseFuture(RespondFuture.DEFAULT_FUTURE_LIFE_TIME, consumer);
    }

    default MessageContext<UID> willResponseFuture() {
        return willResponseFuture(RespondFuture.DEFAULT_FUTURE_LIFE_TIME);
    }

    MessageContext<UID> willResponseFuture(long lifeTime, Consumer<StageableFuture<Message<UID>>> consumer);

    MessageContext<UID> willResponseFuture(long lifeTime);
}

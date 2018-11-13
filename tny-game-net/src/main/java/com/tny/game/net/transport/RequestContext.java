package com.tny.game.net.transport;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface RequestContext<UID> extends MessageContext<UID> {

    @Override
    RequestContext<UID> setAttachment(Object attachment);

    @Override
    RequestContext<UID> willSendFuture(Consumer<MessageSendFuture<UID>> consumer);

    @Override
    RequestContext<UID> willSendFuture();

    default RequestContext<UID> willResponseFuture(Consumer<RespondFuture<UID>> consumer) {
        return willResponseFuture(consumer, RespondFuture.DEFAULT_FUTURE_TIMEOUT);
    }

    default RequestContext<UID> willResponseFuture() {
        return willResponseFuture(RespondFuture.DEFAULT_FUTURE_TIMEOUT);
    }

    RequestContext<UID> willResponseFuture(Consumer<RespondFuture<UID>> consumer, long timeoutMills);

    RequestContext<UID> willResponseFuture(long timeoutMills);

}
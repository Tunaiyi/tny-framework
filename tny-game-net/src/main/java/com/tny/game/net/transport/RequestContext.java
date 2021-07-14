package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

import static com.tny.game.net.transport.TransportConstants.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class RequestContext extends MessageContext {

    /**
     * @param body 设置 Message Body
     * @return 返回 context 自身
     */
    @Override
    public abstract RequestContext setBody(Object body);

    public RequestContext willResponseFuture() {
        return willResponseFuture(RespondFuture.DEFAULT_FUTURE_TIMEOUT);
    }

    public abstract RequestContext willResponseFuture(long timeoutMills);

    @Override
    public RequestContext willWriteFuture() {
        return willWriteFuture(TIMEOUT_NEVER);
    }

    @Override
    public abstract RequestContext willWriteFuture(long timeoutMills);

}
package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class RequestContext<UID> extends MessageContext<UID> {

    /**
     * @param body 设置 Message Body
     * @return 返回 context 自身
     */
    @Override
    public abstract RequestContext<UID> setBody(Object body);

    /**
     * @param tail 设置 Message tail
     * @return 返回 context 自身
     */
    @Override
    public abstract RequestContext<UID> setTail(Object tail);


    public RequestContext<UID> willResponseFuture() {
        return willResponseFuture(RespondFuture.DEFAULT_FUTURE_TIMEOUT);
    }

    public abstract RequestContext<UID> willResponseFuture(long timeoutMills);

    @Override
    public RequestContext<UID> willWriteFuture() {
        return willWriteFuture(-1);
    }

    @Override
    public abstract RequestContext<UID> willWriteFuture(long timeoutMills);

}
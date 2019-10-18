package com.tny.game.net.endpoint;

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class MessageContext<UID> implements SendContext<UID>, MessageSubject {

    /**
     * @return 获取结果码
     */
    public abstract ResultCode getResultCode();

    /**
     * @param body 设置 Message Body
     * @return 返回 context 自身
     */
    public abstract MessageContext<UID> setBody(Object body);

    /**
     * @param tail 设置 Message tail
     * @return 返回 context 自身
     */
    public abstract MessageContext<UID> setTail(Object tail);

    public abstract MessageContext<UID> willWriteFuture();

    public abstract RequestContext<UID> willWriteFuture(long timeoutMills);

    /**
     * 取消
     *
     * @param mayInterruptIfRunning 打断所欲运行
     */
    public abstract void cancel(boolean mayInterruptIfRunning);

    /**
     * 取消
     *
     * @return 是否取消成功
     */
    public abstract void fail(Throwable throwable);

    protected abstract void setWriteMessagePromise(WriteMessagePromise writePromise);

    protected abstract void setRespondFuture(RespondFuture<UID> respondFuture);

    protected abstract WriteMessagePromise getWriteMessagePromise();

    protected abstract boolean isNeedWriteFuture();

    protected abstract boolean isNeedResponseFuture();

}
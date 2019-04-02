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
     * @return 获取消息体
     */
    public abstract Object getBody();

    /**
     * @return 获取尾部
     */
    public abstract Object getTail();

    /**
     * 设置发送超时
     *
     * @param timeout 超时时间
     * @return 返回 context 自身
     */
    public abstract MessageContext<UID> setSendTimeout(long timeout);

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

    /**
     * 发送
     *
     * @param writePromise 发送 future
     * @return 返回是否设置成功
     */
    protected abstract boolean setWritePromise(WriteMessagePromise writePromise);

    /**
     * 取消
     *
     * @param mayInterruptIfRunning 打断所欲运行
     * @return 是否取消成功
     */
    public abstract boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 取消
     *
     * @return 是否取消成功
     */
    public abstract void fail(Throwable throwable);
}
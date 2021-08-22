package com.tny.game.net.endpoint;

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class MessageContext implements SendContext, MessageContent {

	/**
	 * @return 获取结果码
	 */
	public abstract ResultCode getResultCode();

	/**
	 * @param body 设置 Message Body
	 * @return 返回 context 自身
	 */
	public abstract MessageContext setBody(Object body);

	public abstract MessageContext willWriteFuture();

	public abstract MessageContext willWriteFuture(WriteMessageListener listener);

	public abstract MessageContext willWriteFuture(Collection<WriteMessageListener> listeners);

	/**
	 * 取消
	 *
	 * @param mayInterruptIfRunning 打断所欲运行
	 */
	public abstract void cancel(boolean mayInterruptIfRunning);

	/**
	 * 取消
	 */
	protected abstract void fail(Throwable throwable);

	public abstract WriteMessageFuture getWriteMessageFuture();

	protected abstract void setWriteMessagePromise(WriteMessagePromise writePromise);

	protected abstract void setRespondFuture(RespondFuture respondFuture);

	protected abstract boolean isNeedWriteFuture();

	protected abstract boolean isNeedResponseFuture();

}
package com.tny.game.net.transport;

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
		return willResponseFuture(MessageRespondAwaiter.DEFAULT_FUTURE_TIMEOUT);
	}

	public abstract RequestContext willResponseFuture(long timeoutMills);

	public abstract MessageRespondAwaiter getResponseAwaiter();

}
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
    public abstract RequestContext withBody(Object body);

    /**
     * 设置响应等待者
     *
     * @return
     */
    public RequestContext willRespondAwaiter() {
        return willRespondAwaiter(MessageRespondAwaiter.DEFAULT_FUTURE_TIMEOUT);
    }

    /**
     * 设置响应等待者
     *
     * @param timeoutMills 超时时间
     * @return
     */
    public abstract RequestContext willRespondAwaiter(long timeoutMills);

    public abstract MessageRespondAwaiter getResponseAwaiter();

}
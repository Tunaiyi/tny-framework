package com.tny.game.net.rpc;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 4:10 下午
 */
public class RpcSetting {

    private long invokeTimeout = 5000L;

    public long getInvokeTimeout() {
        return invokeTimeout;
    }

    public RpcSetting setInvokeTimeout(long invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
        return this;
    }

}

package com.tny.game.net.rpc;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 4:10 下午
 */
public class RpcRemoteSetting {

    private long invokeTimeout = 5000L;

    private Class<? extends FirstRpcRemoteRouter> defaultRpcRemoteRouter = FirstRpcRemoteRouter.class;

    public long getInvokeTimeout() {
        return invokeTimeout;
    }

    public RpcRemoteSetting setInvokeTimeout(long invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
        return this;
    }

    public Class<? extends FirstRpcRemoteRouter> getDefaultRpcRemoteRouter() {
        return defaultRpcRemoteRouter;
    }

    public RpcRemoteSetting setDefaultRpcRemoteRouter(Class<? extends FirstRpcRemoteRouter> defaultRpcRemoteRouter) {
        this.defaultRpcRemoteRouter = defaultRpcRemoteRouter;
        return this;
    }

}

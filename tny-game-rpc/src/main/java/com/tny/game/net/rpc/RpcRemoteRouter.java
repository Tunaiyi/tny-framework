package com.tny.game.net.rpc;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public interface RpcRemoteRouter {

    RpcRemoterAccess route(RpcRemoterSet servicer, RpcRemoteMethod invoker, RpcRemoteInvokeParams invokeParams);

}

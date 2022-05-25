package com.tny.game.net.rpc;

import com.tny.game.net.message.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:15 上午
 */
public class EndpointRemoteRouter implements RpcRemoteRouter {

    @Override
    public RpcRemoterAccess route(RpcRemoterSet servicer, RpcRemoteMethod invoker, RpcRemoteInvokeParams invokeParams) {
        Messager messager = invokeParams.getReceiver();
        if (messager == null) {
            throw new NullPointerException(format("invoke {} Receiver is null", invoker));
        }
        return servicer.findRemoterAccess(0, messager.getMessagerId());
    }

}

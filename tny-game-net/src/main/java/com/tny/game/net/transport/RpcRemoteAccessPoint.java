package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;

/**
 * RPC接入点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/23 17:07
 **/
public interface RpcRemoteAccessPoint extends Sender {

    RpcAccessIdentify getAccessId();

    ForwardRpcServicer getForwardRpcServicer();

    /**
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isActive();

}

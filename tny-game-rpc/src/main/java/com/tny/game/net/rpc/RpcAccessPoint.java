package com.tny.game.net.rpc;

import com.tny.game.net.transport.*;

/**
 * RPC接入点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/23 17:07
 **/
public interface RpcAccessPoint extends Sender {

    RpcAccessId getAccessId();

}

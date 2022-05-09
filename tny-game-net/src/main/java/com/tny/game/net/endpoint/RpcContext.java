package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * Rpc上下文
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/5 16:59
 **/
public interface RpcContext {

    RpcForwarder getRpcForwarder();

}

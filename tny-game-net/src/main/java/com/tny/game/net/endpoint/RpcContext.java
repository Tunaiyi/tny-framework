package com.tny.game.net.endpoint;

import com.tny.game.net.rpc.*;

/**
 * Rpc上下文
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/5 16:59
 **/
public interface RpcContext {

    /**
     * @return Rpc转发器
     */
    RpcForwarder getRpcForwarder();

}

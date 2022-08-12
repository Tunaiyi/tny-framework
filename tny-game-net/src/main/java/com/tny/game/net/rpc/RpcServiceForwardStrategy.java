package com.tny.game.net.rpc;

import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public interface RpcServiceForwardStrategy extends RpcForwardStrategy {

    /**
     * 使用的服务
     *
     * @return 服务
     */
    RpcServiceType getServiceType();

}
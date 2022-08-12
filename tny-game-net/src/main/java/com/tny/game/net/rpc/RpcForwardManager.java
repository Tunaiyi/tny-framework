package com.tny.game.net.rpc;

import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 3:26 下午
 */
public interface RpcForwardManager {

    RpcForwardSet loadForwardSet(RpcServiceType type);

    RpcForwardSet findForwardSet(RpcServiceType serviceType);

}
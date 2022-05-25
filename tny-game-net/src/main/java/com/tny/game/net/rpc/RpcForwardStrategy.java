package com.tny.game.net.rpc;

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public interface RpcForwardStrategy {

    RpcForwardAccess forward(RpcForwardSet forwarderSet, Message message, RpcForwardHeader forwardHeader);

}

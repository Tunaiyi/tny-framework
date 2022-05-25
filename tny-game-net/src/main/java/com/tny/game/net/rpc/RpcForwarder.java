package com.tny.game.net.rpc;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
@UnitInterface
public interface RpcForwarder {

    RpcForwardAccess forward(Message message, RpcForwardHeader forwardHeader);

}

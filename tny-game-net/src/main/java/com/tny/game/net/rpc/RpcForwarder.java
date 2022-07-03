package com.tny.game.net.rpc;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
@UnitInterface
public interface RpcForwarder {

    /**
     * 转发
     *
     * @param message       消息
     * @param forwardHeader 转发消息头
     * @return 返回
     */
    RpcForwardAccess forward(Message message, RpcForwardHeader forwardHeader);

    /**
     * 广播
     *
     * @param message       消息
     * @param forwardHeader 转发消息头
     * @return 返回
     */
    List<RpcForwardAccess> broadcast(Message message, RpcForwardHeader forwardHeader);

}

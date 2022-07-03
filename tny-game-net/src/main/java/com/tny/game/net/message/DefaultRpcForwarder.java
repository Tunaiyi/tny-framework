package com.tny.game.net.message;

import com.tny.game.common.exception.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.rpc.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/6 01:56
 **/
public class DefaultRpcForwarder implements RpcForwarder {

    private final RpcForwardManager forwardManager;

    private final RpcForwardStrategy defaultStrategy;

    private final Map<RpcServiceType, RpcServiceForwardStrategy> strategyMap;

    public DefaultRpcForwarder(RpcForwardManager forwardManager,
            RpcForwardStrategy defaultStrategy, List<RpcServiceForwardStrategy> strategies) {
        this.forwardManager = forwardManager;
        this.defaultStrategy = defaultStrategy;
        this.strategyMap = strategies.stream().collect(Collectors.toMap(RpcServiceForwardStrategy::getServiceType, ObjectAide::self));
    }

    @Override
    public RpcForwardAccess forward(Message message, RpcForwardHeader forwardHeader) {
        ForwardRpcServicer to = forwardHeader.getTo();
        RpcServiceType serviceType = to.getServiceType();
        if (to.isAccurately()) {
            RpcForwardSet forwarderSet = forwardManager.findForwardSet(serviceType);
            if (forwarderSet == null) {
                return null;
            }
            RpcRemoterAccess access = forwarderSet.findForwardAccess(to);
            if (access != null) {
                return (RpcServiceAccess)access;
            }
        }
        RpcForwardStrategy strategy = strategyMap.get(serviceType);
        if (strategy == null) {
            if (defaultStrategy != null) {
                strategy = defaultStrategy;
            } else {
                throw new NullPointerException("未知道 {} 转发策略");
            }
        }
        RpcForwardSet serviceSet = forwardManager.findForwardSet(serviceType);
        if (serviceSet == null) {
            throw new ResultCodeRuntimeException(NetResultCode.RPC_SERVICE_NOT_AVAILABLE, "未找到可用{}服务", serviceType);
        }
        return strategy.forward(serviceSet, message, forwardHeader);
    }

    @Override
    public List<RpcForwardAccess> broadcast(Message message, RpcForwardHeader forwardHeader) {
        return null;
    }

}

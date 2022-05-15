package com.tny.game.net.message;

import com.tny.game.common.exception.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/6 01:56
 **/
public class DefaultRpcForwarder implements RpcForwarder {

    private final RpcRemoteServiceManager rpcRemoteServiceManager;

    private final RpcForwarderStrategy defaultStrategy;

    private final Map<RpcServiceType, RpcServiceForwarderStrategy> strategyMap;

    public DefaultRpcForwarder(RpcRemoteServiceManager rpcRemoteServiceManager,
            RpcForwarderStrategy defaultStrategy, List<RpcServiceForwarderStrategy> strategies) {
        this.rpcRemoteServiceManager = rpcRemoteServiceManager;
        this.defaultStrategy = defaultStrategy;
        this.strategyMap = strategies.stream().collect(Collectors.toMap(RpcServiceForwarderStrategy::getServiceType, ObjectAide::self));
    }

    @Override
    public RpcRemoteAccessPoint forward(Message message, RpcServicer from, Messager sender, RpcServicer to, Messager receiver) {
        RpcServiceType serviceType = to.getServiceType();
        RpcForwarderStrategy strategy = strategyMap.get(serviceType);
        if (strategy == null) {
            if (defaultStrategy != null) {
                strategy = defaultStrategy;
            } else {
                throw new NullPointerException("未知道 {} 转发策略");
            }
        }
        RpcRemoteServiceSet serviceSet = rpcRemoteServiceManager.find(serviceType);
        if (serviceSet == null) {
            throw new ResultCodeRuntimeException(NetResultCode.RPC_SERVICE_NOT_AVAILABLE, "未找到可用{}服务", serviceType);
        }
        return strategy.forward(serviceSet, message, from, sender, to, receiver);
    }

}

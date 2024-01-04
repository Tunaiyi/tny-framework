/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.message;

import com.tny.game.common.exception.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
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

    private final RpcForwardNodeManager forwardManager;

    private final RpcForwardStrategy defaultStrategy;

    private final Map<RpcServiceType, RpcServiceForwardStrategy> strategyMap;

    public DefaultRpcForwarder(RpcForwardNodeManager forwardManager,
            RpcForwardStrategy defaultStrategy, List<RpcServiceForwardStrategy> strategies) {
        this.forwardManager = forwardManager;
        this.defaultStrategy = defaultStrategy;
        this.strategyMap = strategies.stream().collect(Collectors.toMap(RpcServiceForwardStrategy::getServiceType, ObjectAide::self));
    }

    @Override
    public RpcForwardAccess forward(Message message, RpcForwardHeader forwardHeader) {
        ForwardPoint to = forwardHeader.getTo();
        RpcServiceType serviceType = to.getServiceType();
        if (to.isAppointed()) {
            RpcForwardNodeSet forwarderSet = forwardManager.findForwardNodeSet(serviceType);
            if (forwarderSet == null) {
                return null;
            }
            RpcAccess access = forwarderSet.findForwardAccess(to);
            if (access != null) {
                return (RpcServiceAccess) access;
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
        RpcForwardNodeSet serviceSet = forwardManager.findForwardNodeSet(serviceType);
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

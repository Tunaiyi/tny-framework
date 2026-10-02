/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

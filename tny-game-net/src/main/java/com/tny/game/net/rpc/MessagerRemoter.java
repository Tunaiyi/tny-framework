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
package com.tny.game.net.rpc;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Rpc 转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:15
 **/
public class MessagerRemoter implements RpcRemoteSet, RpcRemoteNode {

    private final MessagerType messagerType;

    private EndpointKeeper<Object, Endpoint<Object>> keeper;

    private final List<MessagerRemoter> remoterList;

    public MessagerRemoter(MessagerType messagerType) {
        this.messagerType = messagerType;
        this.remoterList = Collections.singletonList(this);
    }

    void bind(EndpointKeeper<?, ? extends Endpoint<?>> keeper) {
        this.keeper = as(keeper);
    }

    public MessagerType getMessagerType() {
        return messagerType;
    }

    @Override
    public List<? extends RpcRemoteNode> getOrderRemoteNodes() {
        return remoterList;
    }

    @Override
    public RpcRemoteNode findRemoteNode(int nodeId) {
        return this;
    }

    @Override
    public RpcRemoteAccess findRemoteAccess(int nodeId, long accessId) {
        return getRemoteAccess(accessId);
    }

    @Override
    public int getNodeId() {
        return 0;
    }

    @Override
    public RpcServiceType getServiceType() {
        return null;
    }

    @Override
    public List<? extends RpcRemoteAccess> getOrderRemoteAccesses() {
        return Collections.emptyList();
    }

    @Override
    public RpcRemoteAccess getRemoteAccess(long accessId) {
        Endpoint<?> endpoint = keeper.getEndpoint(accessId);
        if (endpoint != null) {
            return MessagerRemoteAccess.of(endpoint);
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}

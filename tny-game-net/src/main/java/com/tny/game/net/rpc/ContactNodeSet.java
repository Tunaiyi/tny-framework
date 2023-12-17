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
public class ContactNodeSet implements RpcInvokeNodeSet, RpcInvokeNode {

    private final ContactType contactType;

    private EndpointKeeper<Endpoint> keeper;

    private final List<ContactNodeSet> remoterList;

    public ContactNodeSet(ContactType contactType) {
        this.contactType = contactType;
        this.remoterList = Collections.singletonList(this);
    }

    void bind(EndpointKeeper<? extends Endpoint> keeper) {
        if (this.keeper == null) {
            this.keeper = as(keeper);
        }
    }

    public ContactType getContactType() {
        return contactType;
    }

    @Override
    public List<? extends RpcInvokeNode> getOrderInvokeNodes() {
        return remoterList;
    }

    @Override
    public RpcInvokeNode findInvokeNode(int nodeId) {
        return this;
    }

    @Override
    public RpcAccess findInvokeAccess(int nodeId, long accessId) {
        return getAccess(accessId);
    }

    @Override
    public int getNodeId() {
        return 0;
    }

    @Override
    public ContactType getServiceType() {
        return contactType;
    }

    @Override
    public List<? extends RpcAccess> getOrderAccesses() {
        return Collections.emptyList();
    }

    @Override
    public RpcAccess getAccess(long accessId) {
        Endpoint endpoint = keeper.getEndpoint(accessId);
        if (endpoint != null) {
            return RpcEndpointAccess.of(endpoint);
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}

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

import com.tny.game.net.application.*;
import com.tny.game.net.session.*;

import java.util.*;

/**
 * Rpc 转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:15
 **/
public class ContactNodeSet implements RpcInvokeNodeSet, RpcInvokeNode {

    private final ContactType contactType;

    private SessionKeeper keeper;

    private final List<ContactNodeSet> remoterList;

    public ContactNodeSet(ContactType contactType) {
        this.contactType = contactType;
        this.remoterList = Collections.singletonList(this);
    }

    void bind(SessionKeeper keeper) {
        if (this.keeper == null) {
            this.keeper = keeper;
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
        Session session = keeper.getSession(accessId);
        if (session != null) {
            return RpcAccessor.of(session);
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}

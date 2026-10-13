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

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ContactNodeSet.class);

    private final ContactType contactType;

    // volatile：绑定完成对任意路由线程立即可见（net-rpc-registry"接入绑定的安全发布"）
    private volatile SessionKeeper keeper;

    private final List<ContactNodeSet> remoterList;

    public ContactNodeSet(ContactType contactType) {
        this.contactType = contactType;
        this.remoterList = Collections.singletonList(this);
    }

    void bind(SessionKeeper keeper) {
        synchronized (this) {
            if (this.keeper == null) {
                this.keeper = keeper;
                return;
            }
        }
        // 重复绑定：先到者保留、后到忽略并告警（不得静默）
        LOGGER.warn("ContactType {} 已绑定 keeper，忽略第二次绑定 {}", contactType, keeper);
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
        SessionKeeper current = this.keeper;
        if (current == null) {
            // 未绑定：返回空由调用方按服务不可用处置，不得内部异常顶替
            return null;
        }
        Session session = current.getSession(accessId);
        if (session != null) {
            return RpcAccessor.of(session);
        }
        return null;
    }

    @Override
    public boolean isActive() {
        // 活性按真实绑定判定（原恒 true 违反 RpcInvokeNode 契约）
        return this.keeper != null;
    }

}

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

import com.tny.game.common.context.*;
import com.tny.game.net.session.*;

/**
 * Rpc 转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:15
 **/
public class RpcAccessor implements RpcAccess {

    private static final AttrKey<RpcAccessor> RPC_ACCESSOR = AttrKeys.key(RpcAccessor.class, "RPC_ACCESSOR");

    private final Session session;

    public static RpcAccess of(Session session) {
        RpcAccessor access = session.attributes().getAttribute(RPC_ACCESSOR);
        if (access != null) {
            return access;
        }
        return session.attributes().computeIfAbsent(RPC_ACCESSOR, () -> new RpcAccessor(session));
    }

    private RpcAccessor(Session session) {
        this.session = session;
    }

    @Override
    public boolean isActive() {
        return session.isActive();
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public long getAccessId() {
        return session.getContactId();
    }

}

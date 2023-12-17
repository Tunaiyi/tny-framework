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
import com.tny.game.net.endpoint.*;

/**
 * Rpc 转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:15
 **/
public class RpcEndpointAccess implements RpcAccess {

    private static final AttrKey<RpcEndpointAccess> REMOTER_ACCESS = AttrKeys.key(RpcEndpointAccess.class, "REMOTER_ACCESS");

    private final Endpoint endpoint;

    public static RpcAccess of(Endpoint endpoint) {
        RpcEndpointAccess access = endpoint.attributes().getAttribute(REMOTER_ACCESS);
        if (access != null) {
            return access;
        }
        return endpoint.attributes().computeIfAbsent(REMOTER_ACCESS, () -> new RpcEndpointAccess(endpoint));
    }

    private RpcEndpointAccess(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public boolean isActive() {
        return endpoint.isActive();
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public long getAccessId() {
        return endpoint.getContactId();
    }

}

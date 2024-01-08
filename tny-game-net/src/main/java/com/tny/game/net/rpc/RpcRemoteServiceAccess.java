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
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;

/**
 * RpcAccessor接入点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/24 14:27
 **/
public class RpcRemoteServiceAccess implements RpcServiceAccess {

    private final Session session;

    private final ForwardPoint forwardPoint;

    public RpcRemoteServiceAccess(Session session) {
        this.session = session;
        this.forwardPoint = new ForwardPoint(session.getIdentifyToken(RpcAccessIdentify.class));
    }

    @Override
    public long getAccessId() {
        return session.getContactId();
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public RpcAccessIdentify getRpcIdentify() {
        return session.getIdentifyToken(RpcAccessIdentify.class);
    }

    @Override
    public ForwardPoint getForwardPoint() {
        return forwardPoint;
    }

    @Override
    public boolean isActive() {
        return session.isActive();
    }

}

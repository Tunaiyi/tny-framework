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

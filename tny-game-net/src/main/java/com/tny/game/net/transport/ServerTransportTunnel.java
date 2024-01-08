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
package com.tny.game.net.transport;

import com.tny.game.net.application.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class ServerTransportTunnel<E extends NetSession, T extends MessageTransport> extends TransportTunnel<E, T> {

    public ServerTransportTunnel(long id, T transport, NetworkContext context) {
        super(id, transport, NetAccessMode.SERVER, context);
    }

    public ServerTransportTunnel(long id, T transport, E session, NetworkContext context) {
        super(id, transport, session, NetAccessMode.SERVER, context);
    }


    @Override
    protected boolean resetSession(NetSession newSession) {
        Certificate certificate = this.getCertificate();
        if (!certificate.isAuthenticated()) {
            this.session = as(newSession);
            return true;
        }
        return false;
    }

    @Override
    protected void onDisconnected() {
        this.close();
    }

}

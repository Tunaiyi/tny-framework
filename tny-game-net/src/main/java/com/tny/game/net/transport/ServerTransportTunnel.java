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

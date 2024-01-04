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
import com.tny.game.net.endpoint.*;
import com.tny.game.net.rpc.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class ClientTunnel<E extends NetTerminal, T extends MessageTransporter> extends BaseNetTunnel<E, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTunnel.class);

    public ClientTunnel(long id, NetworkContext context) {
        super(id, null, NetAccessMode.CLIENT, context);
    }

    @Override
    protected boolean onOpen() {
        if (!this.isActive()) {
            try {
                this.reset();
                T transport = as(this.endpoint.connect());
                if (transport != null) {
                    this.transporter = transport;
                    this.transporter.bind(this);
                    return true;
                }
            } catch (Exception e) {
                this.disconnect();
                //				throw new TunnelException(e, "{} failed to connect to server", this);
                LOGGER.warn("{} open failed case : {}", this, e.getMessage(), e);
                return false;
            }
        }
        LOGGER.warn("{} is available", this);
        return true;
    }

    @Override
    protected void onOpened() {
        this.endpoint.onConnected(this);
    }

    @Override
    protected void onWriteUnavailable() {
        this.endpoint.reconnect();
    }

    @Override
    protected boolean replaceEndpoint(NetEndpoint endpoint) {
        return this.endpoint == endpoint;
    }

}

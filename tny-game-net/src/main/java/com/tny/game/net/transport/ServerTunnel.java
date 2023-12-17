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

import com.tny.game.net.base.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.rpc.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class ServerTunnel<E extends NetSession, T extends MessageTransporter> extends BaseNetTunnel<E, T> {

    public ServerTunnel(long id, T transporter, NetworkContext context) {
        super(id, transporter, NetAccessMode.SERVER, context);
        this.bind(new AnonymityEndpoint(context, this));
    }

    @Override
    protected boolean replaceEndpoint(NetEndpoint newEndpoint) {
        Certificate certificate = this.getCertificate();
        if (!certificate.isAuthenticated()) {
            MessageCommandBox commandTaskBox = this.endpoint.getCommandBox();
            this.endpoint = as(newEndpoint);
            //            this.endpoint.takeOver(commandTaskBox);
            return true;
        }
        return false;
    }

    @Override
    protected void onDisconnected() {
        this.close();
    }

    @Override
    protected boolean onOpen() {
        T transporter = this.transporter;
        if (transporter == null || !transporter.isActive()) {
            LOGGER.warn("open failed. channel {} is not active", transporter);
            return false;
        }
        return true;
    }

}

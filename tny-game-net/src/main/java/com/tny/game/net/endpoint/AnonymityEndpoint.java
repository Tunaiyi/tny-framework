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
package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class AnonymityEndpoint<UID> extends BaseNetEndpoint<UID> implements NetSession<UID> {

    public AnonymityEndpoint(CertificateFactory<UID> certificateFactory, EndpointContext endpointContext, NetTunnel<UID> tunnel) {
        super(certificateFactory.anonymous(), endpointContext, 0);
        this.tunnel = tunnel;
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        this.close();
    }

}

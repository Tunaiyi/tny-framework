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
import com.tny.game.net.session.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class GeneralClientTunnel extends ClientTransportTunnel<MessageTransport> {

    public GeneralClientTunnel(long id, MessageTransport transport, NetSession session, NetworkContext context,
            TunnelUnavailableWatch watch) {
        super(id, transport, session, context, watch);
    }

    public GeneralClientTunnel(long id, MessageTransport transport, NetworkContext context, TunnelUnavailableWatch watch) {
        super(id, transport, context, watch);
    }
}

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
package com.tny.game.net.relay.link;

import com.tny.game.net.application.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 8:33 下午
 */
public class CommonServerRelayLink extends BaseRelayLink implements ServerRelayLink {

    public CommonServerRelayLink(RelayTransport transport, RpcServiceType serviceType, String service, long instanceId, String key) {
        super(NetAccessMode.SERVER, key, serviceType, service, instanceId, transport);
    }

    @Override
    protected void onOpen() {
        this.write(LinkOpenedPacket.FACTORY, LinkOpenedArguments.success());
    }

    @Override
    public void openTunnel(RelayTunnel tunnel) {
        this.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.success(tunnel));
    }

    @Override
    public void closeTunnel(RelayTunnel tunnel) {
        super.closeTunnel(tunnel);
    }

}

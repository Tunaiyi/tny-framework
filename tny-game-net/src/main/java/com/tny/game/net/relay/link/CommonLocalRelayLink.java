/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.link;

import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 8:33 下午
 */
public class CommonLocalRelayLink extends BaseRelayLink implements LocalRelayLink {

    public CommonLocalRelayLink(RelayTransporter transporter, String service, long instanceId, String key) {
        super(key, service, instanceId, transporter);
    }

    @Override
    protected void onOpen() {
        this.write(LinkOpenedPacket.FACTORY, LinkOpenedArguments.success());
    }

    @Override
    public void openTunnel(RelayTunnel<?> tunnel) {
        this.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.success(tunnel));
    }

    @Override
    public void closeTunnel(RelayTunnel<?> tunnel) {
        super.closeTunnel(tunnel);
    }

}

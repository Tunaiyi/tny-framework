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
package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 9:00 下午
 */
@Unit
public class DefaultServerRelayExplorer extends BaseRelayExplorer<ServerRelayTunnel> implements ServerRelayExplorer {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultServerRelayExplorer.class);

    private final Map<String, ServerRelayLink> linkMap = new ConcurrentHashMap<>();

    @Override
    public RelayLink acceptOpenLink(RelayTransport transport, RpcServiceType serviceType, String service, long instance, String key) {
        CommonServerRelayLink link = new CommonServerRelayLink(transport, serviceType, service, instance, key);
        ServerRelayLink relayLink = linkMap.putIfAbsent(link.getId(), link);
        if (relayLink != null && !relayLink.isCurrentTransport(transport)) {
            link.openOnFailure();
        } else {
            link.open();
        }
        return link;
    }

    @Override
    public void acceptConnectTunnel(NetRelayLink link, NetworkContext networkContext, long instanceId, long tunnelId, String host, int port) {
        ServerRelayLink relayLink = linkMap.get(link.getId());
        if (relayLink == null) {
            link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
            LOGGER.warn("acceptConnectTunnel link[{}] 不存在", link.getId());
            return;
        }
        ServerRelayTransport transport = new DefaultServerRelayTransport(relayLink);
        ServerRelayTunnel replayTunnel = new GeneralServerRelayTunnel(
                instanceId, tunnelId, transport, new InetSocketAddress(host, port), networkContext);
        putTunnel(replayTunnel);
        relayLink.openTunnel(replayTunnel);
        replayTunnel.open();
    }

    @Override
    public void switchTunnelLink(NetRelayLink link, long instanceId, long tunnelId) {
        ServerRelayTunnel tunnel = this.getTunnel(instanceId, tunnelId);
        if (tunnel == null) {
            link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
            LOGGER.warn("switchTunnelLink tunnel[{}-{}] 不存在", instanceId, tunnelId);
            return;
        }
        ServerRelayLink relayLink = linkMap.get(link.getId());
        if (relayLink == null) {
            link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
            LOGGER.warn("switchTunnelLink link[{}] 不存在", link.getId());
            return;
        }
        if (!tunnel.switchLink(relayLink)) { // 切换失败
            link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
            LOGGER.warn("switchTunnelLink tunnel[{}-{}] 切换 link[{}] 失败", instanceId, tunnelId, link.getId());
        }
    }

    public void close() {
        linkMap.forEach((k, link) -> link.close());
        linkMap.clear();
    }

}

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
import com.tny.game.net.relay.link.listener.*;
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
            // 注册条目随链路失去承载能力摘除：close 路径内部必经 doDisconnect，
            // onDisconnect 单点覆盖物理断线与主动关闭（relay-link 规格；僵尸条目曾永久滞留）
            link.eventWatch().add(new LinkUnregisterListener(link));
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

    /** 链路断开/关闭时值相等 CAS 摘除自身条目（不误摘同键重建后的新链路） */
    private final class LinkUnregisterListener implements RelayLinkListener {
        private final ServerRelayLink link;

        LinkUnregisterListener(ServerRelayLink link) {
            this.link = link;
        }

        @Override
        public void onOpen(NetRelayLink link) {
        }

        @Override
        public void onDisconnect(NetRelayLink link) {
            // 引用相等判定：BaseRelayLink.equals 基于标识字段，同键重建后旧链路的迟到事件
            // 用 remove(key,value) 会经 equals 误摘新链路（用例⑨实测），computeIfPresent+== 才可靠
            linkMap.computeIfPresent(this.link.getId(), (k, current) -> current == this.link ? null : current);
        }

        @Override
        public void onClosing(NetRelayLink link) {
        }

        @Override
        public void onClosed(NetRelayLink link) {
        }
    }

}

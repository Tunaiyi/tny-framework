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
package com.tny.game.net.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * 远程 RelayPacket 处理器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public class RelayPacketServerProcessor extends BaseRelayPacketProcessor {

    private final ServerRelayExplorer serverRelayExplorer;

    private final NetworkContext networkContext;

    public RelayPacketServerProcessor(ServerRelayExplorer serverRelayExplorer, NetworkContext networkContext) {
        super(serverRelayExplorer);
        this.serverRelayExplorer = serverRelayExplorer;
        this.networkContext = networkContext;
    }

    @Override
    public void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet) {
        checkLink(link, packet);
        TunnelRelayArguments arguments = packet.getArguments();
        ServerRelayTunnel<?> tunnel = serverRelayExplorer.getTunnel(arguments.getInstanceId(), arguments.getTunnelId());
        if (tunnel == null) {
            RelayPacket.release(packet);
            link.write(TunnelDisconnectPacket.FACTORY, new TunnelVoidArguments(arguments));
            LOGGER.warn("{} 转发消息 {} 到 tunnel[{}], 未找到目标 tunnel", link, packet, arguments.getTunnelId());
            return;
        }
        Message message = arguments.getMessage();
        if (message == null) {
            LOGGER.warn("{} 转发消息 {} 到 tunnel[{}], message 为 null", link, packet, arguments.getTunnelId());
            return;
        }
        tunnel.receive(message);
    }

    @Override
    public void onLinkOpen(RelayTransporter transporter, LinkOpenPacket packet) {
        LinkOpenArguments arguments = packet.getArguments();
        var link = serverRelayExplorer.acceptOpenLink(transporter, arguments.getService(), arguments.getInstance(), arguments.getKey());
        LOGGER.info("#{} [ 接受连接 ]", link);
    }

    @Override
    public void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet) {
        checkLink(link, packet);
        TunnelConnectArguments arguments = packet.getArguments();
        LOGGER.info("#{} [ Tunnel({}) 连接接受 ]", link, arguments.getTunnelId());
        serverRelayExplorer.acceptConnectTunnel(link, this.networkContext,
                arguments.getInstanceId(), arguments.getTunnelId(), arguments.getIp(), arguments.getPort());
    }

    @Override
    public void onTunnelSwitchLink(NetRelayLink link, TunnelSwitchLinkPacket packet) {
        checkLink(link, packet);
        TunnelVoidArguments arguments = packet.getArguments();
        LOGGER.info("#{} [ Tunnel({}) 切换连接 ]", link.getId(), arguments.getTunnelId());
        serverRelayExplorer.switchTunnelLink(link, arguments.getInstanceId(), arguments.getTunnelId());
    }

}

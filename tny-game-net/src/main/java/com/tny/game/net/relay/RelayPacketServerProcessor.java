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
package com.tny.game.net.relay;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
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

    private final RpcMonitor rpcMonitor;

    public final NetBootstrapSetting setting;

    public RelayPacketServerProcessor(ServerRelayExplorer serverRelayExplorer, NetworkContext networkContext) {
        super(serverRelayExplorer);
        this.serverRelayExplorer = serverRelayExplorer;
        this.networkContext = networkContext;
        this.setting = networkContext.getSetting();
        this.rpcMonitor = networkContext.getRpcMonitor();
    }

    @Override
    public void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet) {
        checkLink(link, packet);
        TunnelRelayArguments arguments = packet.getArguments();
        ServerRelayTunnel tunnel = serverRelayExplorer.getTunnel(arguments.getInstanceId(), arguments.getTunnelId());
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
        if (message instanceof NetMessage netMessage) {
            RpcMessageAide.ignoreHeaders(netMessage, setting.getReadIgnoreHeaders());
            // var rpcContext = RpcTransactionContext.createEnter(tunnel, (NetMessage)message, true);
            // rpcMonitor.onReceive(rpcContext);
            tunnel.receive(netMessage);
        }
    }

    @Override
    public void onLinkOpen(RelayTransport transport, LinkOpenPacket packet) {
        LinkOpenArguments arguments = packet.getArguments();
        var link = serverRelayExplorer.acceptOpenLink(transport,
                arguments.getServiceType(), arguments.getService(), arguments.getInstance(), arguments.getKey());
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

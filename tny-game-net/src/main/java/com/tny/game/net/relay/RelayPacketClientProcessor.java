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

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * 本地数据包处理器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public class RelayPacketClientProcessor extends BaseRelayPacketProcessor {

    private final ClientRelayExplorer remoteRelayExplorer;

    private final RpcMonitor rpcMonitor;

    public final NetBootstrapSetting setting;

    public RelayPacketClientProcessor(ClientRelayExplorer remoteRelayExplorer, NetworkContext networkContext) {
        super(remoteRelayExplorer);
        this.remoteRelayExplorer = remoteRelayExplorer;
        this.setting = networkContext.getSetting();
        this.rpcMonitor = networkContext.getRpcMonitor();
    }

    @Override
    public void onLinkOpen(RelayTransporter transporter, LinkOpenPacket packet) {
    }

    @Override
    public void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet) {
    }

    @Override
    public void onTunnelSwitchLink(NetRelayLink link, TunnelSwitchLinkPacket packet) {
    }

    @Override
    public void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet) {
        checkLink(link, packet);
        TunnelRelayArguments arguments = packet.getArguments();
        ClientRelayTunnel tunnel = remoteRelayExplorer.getTunnel(arguments.getInstanceId(), arguments.getTunnelId());
        if (tunnel == null) {
            RelayPacket.release(packet);
            link.write(TunnelDisconnectPacket.FACTORY, new TunnelVoidArguments(arguments));
            LOGGER.warn("{} 转发消息 {} 到 tunnel[{}], 未找到目标 tunnel", link, packet, arguments.getTunnelId());
            return;
        }
        NetMessage message = (NetMessage) arguments.getMessage();
        if (message != null) {
            RpcMessageAide.ignoreHeaders(message, setting.getWriteIgnoreHeaders());
            var rpcContext = RpcTransactionContext.createRelay(link, message, rpcMonitor, false);
            rpcMonitor.onReceive(rpcContext);
            rpcContext.transfer(tunnel, RpcTransactionContext.relayOperation(message));
            tunnel.write(message, null);
            rpcContext.completeSilently();
        }
    }

}

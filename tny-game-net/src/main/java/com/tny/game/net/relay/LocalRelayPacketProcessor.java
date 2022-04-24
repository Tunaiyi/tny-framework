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
public class LocalRelayPacketProcessor extends BaseRelayPacketProcessor {

    private final LocalRelayExplorer localRelayExplorer;

    private final NetworkContext networkContext;

    public LocalRelayPacketProcessor(LocalRelayExplorer localRelayExplorer, NetworkContext networkContext) {
        super(localRelayExplorer);
        this.localRelayExplorer = localRelayExplorer;
        this.networkContext = networkContext;
    }

    @Override
    public void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet) {
        checkLink(link, packet);
        TunnelRelayArguments arguments = packet.getArguments();
        LocalRelayTunnel<?> tunnel = localRelayExplorer.getTunnel(arguments.getInstanceId(), arguments.getTunnelId());
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
        LOGGER.info("#RelayLink({}) [{} ==> {}]  接受连接", NetRelayLink.idOf(arguments.getService(), arguments.getInstance(), arguments.getKey()),
                transporter.getLocalAddress(), transporter.getRemoteAddress());
        localRelayExplorer.acceptOpenLink(transporter, arguments.getService(), arguments.getInstance(), arguments.getKey());
    }

    @Override
    public void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet) {
        checkLink(link, packet);
        TunnelConnectArguments arguments = packet.getArguments();
        LOGGER.info("#RelayLink({}) [{} ==> {}] #Tunnel# 连接接受 [ RelayTunnel({}) ]",
                link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
        localRelayExplorer.acceptConnectTunnel(link, this.networkContext,
                arguments.getInstanceId(), arguments.getTunnelId(), arguments.getIp(), arguments.getPort());
    }

    @Override
    public void onTunnelSwitchLink(NetRelayLink link, TunnelSwitchLinkPacket packet) {
        checkLink(link, packet);
        TunnelVoidArguments arguments = packet.getArguments();
        LOGGER.info("#RelayLink({}) [{} ==> {}] #Tunnel# 切换连接 [ RelayTunnel({}) ]",
                link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
        localRelayExplorer.switchTunnelLink(link, arguments.getInstanceId(), arguments.getTunnelId());
    }

}

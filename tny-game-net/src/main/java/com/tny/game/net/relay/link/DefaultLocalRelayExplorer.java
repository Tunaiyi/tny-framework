package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
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
public class DefaultLocalRelayExplorer extends BaseRelayExplorer<LocalRelayTunnel<?>> implements LocalRelayExplorer {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocalRelayExplorer.class);

    private final Map<String, LocalRelayLink> linkMap = new ConcurrentHashMap<>();

    @Override
    public void acceptOpenLink(RelayTransporter transporter, String service, long instance, String key) {
        CommonLocalRelayLink link = new CommonLocalRelayLink(transporter, service, instance, key);
        LocalRelayLink relayLink = linkMap.putIfAbsent(link.getId(), link);
        if (relayLink != null && !relayLink.isCurrentTransporter(transporter)) {
            link.openOnFailure();
        } else {
            link.open();
        }
    }

    @Override
    public void acceptConnectTunnel(NetRelayLink link, NetworkContext networkContext, long instanceId, long tunnelId, String host, int port) {
        LocalRelayLink relayLink = linkMap.get(link.getId());
        if (relayLink == null) {
            link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
            LOGGER.warn("acceptConnectTunnel link[{}] 不存在", link.getId());
            return;
        }
        LocalRelayMessageTransporter transporter = new DefaultLocalRelayMessageTransporter(relayLink);
        LocalRelayTunnel<?> replayTunnel = new GeneralLocalRelayTunnel<>(
                instanceId, tunnelId, transporter, new InetSocketAddress(host, port), networkContext);
        putTunnel(replayTunnel);
        relayLink.openTunnel(replayTunnel);
        replayTunnel.open();
    }

    @Override
    public void switchTunnelLink(NetRelayLink link, long instanceId, long tunnelId) {
        LocalRelayTunnel<?> tunnel = this.getTunnel(instanceId, tunnelId);
        if (tunnel == null) {
            link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
            LOGGER.warn("switchTunnelLink tunnel[{}-{}] 不存在", instanceId, tunnelId);
            return;
        }
        LocalRelayLink relayLink = linkMap.get(link.getId());
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

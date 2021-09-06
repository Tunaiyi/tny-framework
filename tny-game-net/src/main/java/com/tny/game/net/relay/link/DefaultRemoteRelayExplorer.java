package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 9:00 下午
 */
@Unit
public class DefaultRemoteRelayExplorer extends BaseRelayExplorer<RemoteRelayTunnel<?>> implements RemoteRelayExplorer {

	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultRemoteRelayExplorer.class);

	private final Map<String, RemoteRelayLink> linkMap = new ConcurrentHashMap<>();

	@Override
	public void acceptOpenLink(NetRelayTransporter transporter, String clusterId, long instance, String key) {
		CommonRemoteRelayLink link = new CommonRemoteRelayLink(transporter, clusterId, instance, key);
		RemoteRelayLink relayLink = linkMap.putIfAbsent(link.getId(), link);
		if (relayLink != null && !Objects.equals(relayLink.getTransporter(), transporter)) {
			link.openOnFailure();
		} else {
			link.open();
		}
	}

	@Override
	public void acceptConnectTunnel(NetRelayLink link, NetworkContext networkContext, long instanceId, long tunnelId, String host, int port) {
		RemoteRelayLink relayLink = linkMap.get(link.getId());
		if (relayLink == null) {
			link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
			LOGGER.warn("acceptConnectTunnel link[{}] 不存在", link.getId());
			return;
		}
		RemoteRelayMessageTransporter<UID> transporter = new DefaultRemoteRelayMessageTransporter<>(relayLink);
		RemoteRelayTunnel<?> replayTunnel = new GeneralRemoteRelayTunnel<>(
				instanceId, tunnelId, transporter, new InetSocketAddress(host, port), networkContext);
		putTunnel(replayTunnel);
		relayLink.openTunnel(replayTunnel);
		replayTunnel.open();
	}

	@Override
	public void switchTunnelLink(NetRelayLink link, long instanceId, long tunnelId) {
		RemoteRelayTunnel<?> tunnel = this.getTunnel(instanceId, tunnelId);
		if (tunnel == null) {
			link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.failure(instanceId, tunnelId));
			LOGGER.warn("switchTunnelLink tunnel[{}-{}] 不存在", instanceId, tunnelId);
			return;
		}
		RemoteRelayLink relayLink = linkMap.get(link.getId());
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

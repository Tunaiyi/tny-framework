package com.tny.game.net.relay.link;

import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 9:00 下午
 */
public class DefaultRemoteRelayExplorer implements RemoteRelayExplorer {

	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultRemoteRelayExplorer.class);

	private final Map<String, RemoteRelayLink> linkMap = new ConcurrentHashMap<>();

	public DefaultRemoteRelayExplorer() {
	}

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
	public void acceptConnectTunnel(NetRelayLink link, long tunnelId, String ip, int port) {
		RemoteRelayLink relayLink = linkMap.get(link.getId());
		if (relayLink != null) {
			relayLink.acceptTunnel(tunnelId, ip, port);
		} else {
			link.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.ofResult(tunnelId, false));
			LOGGER.warn("link[{}] 不存在", link.getId());
		}
	}

	public void close() {
		linkMap.forEach((k, link) -> link.close());
		linkMap.clear();
	}

}

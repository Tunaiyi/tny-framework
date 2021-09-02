package com.tny.game.net.relay.link;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 8:33 下午
 */
public class CommonRemoteRelayLink extends BaseRelayLink implements RemoteRelayLink {

	private final NetworkContext<?> networkContext;

	public CommonRemoteRelayLink(NetRelayTransporter transporter, String clusterId, long instanceId, String key) {
		super(clusterId, instanceId, key, transporter);
		this.networkContext = transporter.getContext();
	}

	@Override
	protected void onOpen() {
		this.write(LinkOpenedPacket.FACTORY, LinkOpenedArguments.success());
	}

	@Override
	public <UID> NetTunnel<UID> acceptTunnel(long tunnelId, String host, int port) {
		MessageTransporter<UID> transporter = new RemoteRelayMessageTransporter<>(this);
		NetRelayTunnel<UID> tunnel = new GeneralRemoteRelayTunnel<>(tunnelId, transporter, new InetSocketAddress(host, port),
				as(this.networkContext));
		if (addTunnel(tunnel)) {
			tunnel.open();
			return tunnel;
		}
		return null;
	}

}

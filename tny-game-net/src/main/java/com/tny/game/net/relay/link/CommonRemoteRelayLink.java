package com.tny.game.net.relay.link;

import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 8:33 下午
 */
public class CommonRemoteRelayLink extends BaseRelayLink implements RemoteRelayLink {

	public CommonRemoteRelayLink(NetRelayTransporter transporter, String clusterId, long instanceId, String key) {
		super(key, clusterId, instanceId, transporter);
	}

	@Override
	protected void onOpen() {
		this.write(LinkOpenedPacket.FACTORY, LinkOpenedArguments.success());
	}

	@Override
	public void openTunnel(RelayTunnel<?> tunnel) {
		this.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.success(tunnel));
	}

}

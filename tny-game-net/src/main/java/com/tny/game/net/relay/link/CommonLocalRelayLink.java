package com.tny.game.net.relay.link;

import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 8:33 下午
 */
public class CommonLocalRelayLink extends BaseRelayLink implements LocalRelayLink {

	public CommonLocalRelayLink(NetRelayTransporter transporter, String service, long instanceId, String key) {
		super(key, service, instanceId, transporter);
	}

	@Override
	protected void onOpen() {
		this.write(LinkOpenedPacket.FACTORY, LinkOpenedArguments.success());
	}

	@Override
	public void openTunnel(RelayTunnel<?> tunnel) {
		this.write(TunnelConnectedPacket.FACTORY, TunnelConnectedArguments.success(tunnel));
	}

	@Override
	public void closeTunnel(RelayTunnel<?> tunnel) {
		super.closeTunnel(tunnel);
	}

}

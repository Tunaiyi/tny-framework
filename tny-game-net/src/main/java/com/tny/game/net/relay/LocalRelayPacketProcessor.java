package com.tny.game.net.relay;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public class LocalRelayPacketProcessor extends BaseRelayPacketProcessor {

	@Override
	public void onLinkOpen(NetRelayTransporter transporter, LinkOpenPacket packet) {
	}

	@Override
	public void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet) {
	}

}

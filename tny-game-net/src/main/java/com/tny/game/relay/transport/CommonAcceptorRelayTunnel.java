package com.tny.game.relay.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class CommonAcceptorRelayTunnel<UID> extends BaseServerTunnel<UID, NetSession<UID>, MessageTransporter<UID>> implements AcceptorRelayTunnel<UID> {

	private final InetSocketAddress remoteAddress;

	public CommonAcceptorRelayTunnel(long id, MessageTransporter<UID> transporter, NetBootstrapContext<UID> bootstrapContext) {
		super(id, transporter, bootstrapContext);
		this.remoteAddress = transporter.getRemoteAddress();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

}

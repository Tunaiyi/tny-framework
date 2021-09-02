package com.tny.game.net.relay.link;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class GeneralRemoteRelayTunnel<UID> extends BaseServerTunnel<UID, NetSession<UID>, MessageTransporter<UID>> implements RemoteRelayTunnel<UID> {

	private final InetSocketAddress remoteAddress;

	public GeneralRemoteRelayTunnel(long id, MessageTransporter<UID> transporter,
			InetSocketAddress remoteAddress, NetworkContext<UID> context) {
		super(id, transporter, context);
		this.remoteAddress = remoteAddress;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public WriteMessageFuture relay(Message message, boolean needPromise) {
		WriteMessagePromise promise = needPromise ? this.createWritePromise() : null;
		this.write(message, promise);
		return promise;
	}

	@Override
	public void closeOnLink(NetRelayLink link) {
		this.close();
	}

}

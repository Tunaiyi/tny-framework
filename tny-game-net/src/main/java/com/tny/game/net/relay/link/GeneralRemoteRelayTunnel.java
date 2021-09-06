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

	private final long instanceId;

	private final InetSocketAddress remoteAddress;

	private final RemoteRelayMessageTransporter<UID> transporter;

	public GeneralRemoteRelayTunnel(long instanceId, long id, RemoteRelayMessageTransporter<UID> transporter,
			InetSocketAddress remoteAddress, NetworkContext context) {
		super(id, transporter, context);
		this.transporter = transporter;
		this.instanceId = instanceId;
		this.remoteAddress = remoteAddress;
	}

	@Override
	public long getInstanceId() {
		return instanceId;
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
	public boolean switchLink(RemoteRelayLink link) {
		return this.transporter.switchLink(link);
	}

	//	@Override
	//	public void onLinkDisconnect(NetRelayLink link) {
	//		this.close();
	//	}
	//
	//	@Override
	//	public void disconnectOnLink(NetRelayLink link) {
	//		this.disconnect();
	//	}

}

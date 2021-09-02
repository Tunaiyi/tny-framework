package com.tny.game.net.relay.link;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 1:02 下午
 */
public class RemoteRelayMessageTransporter<UID> implements MessageTransporter<UID> {

	private NetTunnel<UID> tunnel;

	private final RemoteRelayLink link;

	public RemoteRelayMessageTransporter(RemoteRelayLink link) {
		this.link = link;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return tunnel.getRemoteAddress();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return link.getLocalAddress();
	}

	@Override
	public boolean isActive() {
		return (tunnel != null && tunnel.isActive()) && link.isActive();
	}

	@Override
	public void close() {
		link.destroyTunnel(tunnel);
	}

	@Override
	public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
		return link.relay(this.tunnel.getId(), message, promise);
	}

	@Override
	public WriteMessageFuture write(MessageAllocator maker, MessageFactory factory, MessageContext context) throws NetException {
		return link.relay(this.tunnel.getId(), maker, factory, context);
	}

	@Override
	public void bind(NetTunnel<UID> tunnel) {
		if (this.tunnel != null) {
			this.tunnel = tunnel;
		}
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return link.createWritePromise();
	}

}

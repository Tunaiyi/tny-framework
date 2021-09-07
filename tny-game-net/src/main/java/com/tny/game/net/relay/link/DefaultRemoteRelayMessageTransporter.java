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
public class DefaultRemoteRelayMessageTransporter<UID> implements RemoteRelayMessageTransporter<UID> {

	private RemoteRelayTunnel<UID> tunnel;

	private volatile RemoteRelayLink link;

	public DefaultRemoteRelayMessageTransporter(RemoteRelayLink link) {
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
		return link.isActive();
	}

	@Override
	public void close() {
		link.closeTunnel(tunnel);
	}

	@Override
	public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
		return link.relay(this.tunnel, message, promise);
	}

	@Override
	public WriteMessageFuture write(MessageAllocator maker, MessageFactory factory, MessageContext context) throws NetException {
		return link.relay(this.tunnel, maker, factory, context);
	}

	@Override
	public void bind(NetTunnel<UID> tunnel) {
		if (this.tunnel == null) {
			this.tunnel = (RemoteRelayTunnel<UID>)tunnel;
		}
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return link.createWritePromise();
	}

	@Override
	public boolean switchLink(RemoteRelayLink link) {
		if (this.link == null) {
			return false;
		}
		if (link == null) {
			return false;
		}
		synchronized (this) {
			if (this.link == null) {
				return false;
			}
			if (this.tunnel.getStatus() == TunnelStatus.OPEN) {
				if (link.getInstanceId() == this.link.getInstanceId()) {
					this.link = link;
				}
				return true;
			}
			return false;
		}
	}

}
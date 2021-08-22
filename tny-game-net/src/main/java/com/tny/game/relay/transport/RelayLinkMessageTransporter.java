package com.tny.game.relay.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 转发通道实现的消息发送器
 * <p>
 * 通过此发送器的消息都会通过 link 转发出去.
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:21 下午
 */
public class RelayLinkMessageTransporter<UID> implements MessageTransporter<UID> {

	private final NetRelayLinkAllocator linkAllocator;

	private NetRelayLink relayLink;

	private final InetSocketAddress remoteAddress;

	private NetTunnel<UID> tunnel;

	private final AtomicBoolean closed = new AtomicBoolean(false);

	public RelayLinkMessageTransporter(NetRelayLinkAllocator linkAllocator, InetSocketAddress remoteAddress) {
		this.linkAllocator = linkAllocator;
		this.remoteAddress = remoteAddress;
	}

	@Override
	public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
		NetRelayLink link = this.writeRelayLink(promise);
		if (link != null) {
			return link.relay(this.tunnel.getId(), message, promise);
		}
		return promise;
	}

	@Override
	public WriteMessageFuture write(MessageAllocator allocator, MessageFactory factory, MessageContext context) throws NetException {
		WriteMessagePromise promise = as(context.getWriteMessageFuture());
		NetRelayLink link = this.writeRelayLink(promise);
		if (link != null) {
			return link.relay(this.tunnel.getId(), allocator, factory, context);
		}
		return promise;
	}

	private NetRelayLink writeRelayLink(WriteMessagePromise promise) {
		NetRelayLink link = this.allotLink();
		if (link == null || !link.isActive()) {
			promise.cancel(true);
			return null;
		}
		return link;
	}

	private NetRelayLink allotLink() {
		if (this.relayLink == null || !this.relayLink.isActive()) {
			NetRelayLink link = linkAllocator.allot(tunnel);
			if (link != null && link.isActive()) {
				return this.relayLink = link;
			}
			return null;
		}
		return this.relayLink;
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		NetRelayLink link = allotLink();
		if (link != null) {
			return link.createWritePromise();
		}
		return null;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		NetRelayLink link = allotLink();
		if (link != null) {
			return link.getLocalAddress();
		}
		return null;
	}

	@Override
	public boolean isActive() {
		return this.linkAllocator.isActive() && !closed.get();
	}

	@Override
	public void close() {
		closed.compareAndSet(false, true);
	}

	@Override
	public void bind(NetTunnel<UID> tunnel) {
		this.tunnel = tunnel;
		this.relayLink = linkAllocator.allot(tunnel);
	}

}

package com.tny.game.relay.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.relay.packet.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.InetSocketAddress;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/19 4:39 下午
 */
public abstract class BaseRelayLink<UID> implements NetRelayLink {

	private final String type;

	private final int node;

	private final int line;

	private volatile RelayLinkStatus status = RelayLinkStatus.INIT;

	private final RelayTransporter transporter;

	private final long createAt;

	public BaseRelayLink(String type, int node, int line, RelayTransporter transporter) {
		this.type = type;
		this.node = node;
		this.line = line;
		this.transporter = transporter;
		this.transporter.bind(this);
		if (this.transporter.isActive()) {
			this.status = RelayLinkStatus.OPEN;
		}
		this.createAt = System.currentTimeMillis();
	}

	@Override
	public int getServeNode() {
		return node;
	}

	@Override
	public String getServeType() {
		return type;
	}

	@Override
	public int getServeLine() {
		return line;
	}

	@Override
	public long getCreateTime() {
		return this.createAt;
	}

	@Override
	public RelayLinkStatus getStatus() {
		return this.status;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return this.transporter.getRemoteAddress();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return this.transporter.getLocalAddress();
	}

	@Override
	public boolean isActive() {
		return this.status == RelayLinkStatus.OPEN && this.isConnected();
	}

	private boolean isConnected() {
		RelayTransporter transporter = this.transporter;
		return transporter != null && transporter.isActive();
	}

	@Override
	public WriteMessageFuture relay(long tunnelId, Message message, WriteMessagePromise promise) {
		return this.transporter.write(new TunnelRelayPacket(tunnelId, message), promise);
	}

	@Override
	public WriteMessageFuture relay(long tunnelId, MessageAllocator allocator, MessageFactory factory, MessageContext context) {
		return this.transporter.write(
				() -> new TunnelRelayPacket(tunnelId, allocator.allocate(factory, context)),
				as(context.getWriteMessageFuture(), WriteMessagePromise.class));
	}

	@Override
	public WriteMessageFuture write(RelayPacket<?> packet, boolean promise) {
		return this.transporter.write(packet, promise ? this.transporter.createWritePromise() : null);
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return this.transporter.createWritePromise();
	}

	@Override
	public void open() {
		if (this.status != RelayLinkStatus.INIT || !this.transporter.isActive()) {
			return;
		}
		synchronized (this) {
			if (this.status != RelayLinkStatus.INIT || !this.transporter.isActive()) {
				return;
			}
			this.status = RelayLinkStatus.OPEN;
			this.onOpen();
		}
	}

	private void onOpen() {
	}

	@Override
	public void close() {
		if (this.status.isCloseStatus()) {
			return;
		}
		synchronized (this) {
			if (this.status.isCloseStatus()) {
				return;
			}
			this.status = RelayLinkStatus.CLOSING;
			//			this.tunnelMap.forEach((id, tunnel) -> tunnel.close());
			this.status = RelayLinkStatus.CLOSED;
			this.onClose();
		}
	}

	protected void onClose() {
		RelayTransporter transporter = this.transporter;
		if (transporter != null && transporter.isActive()) {
			transporter.close();
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("link", this.transporter.getLocalAddress())
				.toString();
	}

}

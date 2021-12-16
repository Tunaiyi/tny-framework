package com.tny.game.net.relay.link;

import com.tny.game.common.event.firer.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.listener.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.builder.*;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/19 4:39 下午
 */
public abstract class BaseRelayLink implements NetRelayLink {

	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private long latelyHeartbeatTime = System.currentTimeMillis();

	/**
	 * 连接 id
	 */
	private final String id;

	/**
	 * 唯一 key
	 */
	private final String key;

	/**
	 * 集群id
	 */
	private final String service;

	/**
	 * 集群实例(节点) id
	 */
	private final long instanceId;

	/**
	 * 创建时间
	 */
	private final long createAt;

	/**
	 * 转发链路状态
	 */
	private volatile RelayLinkStatus status = RelayLinkStatus.INIT;

	/**
	 * 转发发送器
	 */
	protected final NetRelayTransporter transporter;

	/**
	 * 转发关掉事件
	 */
	private final EventFirer<RelayLinkListener, NetRelayLink> event = EventFirers.firer(RelayLinkListener.class);

	/**
	 * 数据包 id 创建器
	 */
	private final AtomicInteger packetIdCreator = new AtomicInteger();

	public BaseRelayLink(String key, String service, long instanceId, NetRelayTransporter transporter) {
		this.key = key;
		this.service = service;
		this.instanceId = instanceId;
		this.id = NetRelayLink.idOf(this);
		this.transporter = transporter;
		this.createAt = System.currentTimeMillis();
		this.transporter.bind(this);
	}

	@Override
	public long getInstanceId() {
		return instanceId;
	}

	@Override
	public String getService() {
		return service;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getKey() {
		return key;
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
	public RelayTransporter getTransporter() {
		return transporter;
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
	public void heartbeat() {
		this.latelyHeartbeatTime = System.currentTimeMillis();
	}

	@Override
	public long getLatelyHeartbeatTime() {
		return latelyHeartbeatTime;
	}

	@Override
	public MessageWriteAwaiter relay(RelayTunnel<?> tunnel, Message message, MessageWriteAwaiter awaiter) {
		return this.transporter.write(new TunnelRelayPacket(createPacketId(), tunnel.getInstanceId(), tunnel.getId(), message), awaiter);
	}

	@Override
	public MessageWriteAwaiter relay(RelayTunnel<?> tunnel, MessageAllocator allocator, MessageFactory factory, MessageContext context) {
		return this.transporter.write(
				() -> new TunnelRelayPacket(createPacketId(), tunnel.getInstanceId(), tunnel.getId(), allocator.allocate(factory, context)),
				context.getWriteAwaiter());
	}

	@Override
	public <P extends RelayPacket<A>, A extends RelayPacketArguments> MessageWriteAwaiter write(RelayPacketFactory<P, A> factory, A arguments,
			boolean promise) {
		return this.transporter.write(factory.createPacket(createPacketId(), arguments, System.currentTimeMillis()),
				promise ? new MessageWriteAwaiter() : null);
	}

	@Override
	public void ping() {
		this.transporter.write(LinkHeartBeatPacket.ping(createPacketId()), null);
	}

	@Override
	public void pong() {
		this.transporter.write(LinkHeartBeatPacket.pong(createPacketId()), null);
	}

	@Override
	public EventSource<RelayLinkListener> event() {
		return event;
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
			this.heartbeat();
			event.fire(RelayLinkListener::onOpen, this);
		}
	}

	protected void onOpen() {
	}

	public void openOnFailure() {
		if (this.status != RelayLinkStatus.INIT) {
			return;
		}
		synchronized (this) {
			if (this.status != RelayLinkStatus.INIT) {
				return;
			}
			this.write(LinkOpenedPacket.FACTORY, LinkOpenedArguments.failure(), true);
			this.doDisconnect();
			this.onOpenFailure();
		}
	}

	protected void onOpenFailure() {
	}

	@Override
	public void disconnect() {
		if (this.status != RelayLinkStatus.OPEN) {
			return;
		}
		synchronized (this) {
			if (this.status != RelayLinkStatus.OPEN) {
				return;
			}
			this.status = RelayLinkStatus.DISCONNECT;
			this.doDisconnect();
		}
	}

	protected void onDisconnect() {
	}

	private void doDisconnect() {
		RelayTransporter transporter = this.transporter;
		if (transporter != null && transporter.isActive()) {
			transporter.close();
		}
		event.fire(RelayLinkListener::onDisconnect, this);
		LOGGER.info("RelayLink [{}:{}] 转发链接断开", this, this.status);
		this.onDisconnect();
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
			event.fire(RelayLinkListener::onClosing, this);
			this.write(LinkClosePacket.FACTORY, null);
			this.doDisconnect();
			this.status = RelayLinkStatus.CLOSED;
			event.fire(RelayLinkListener::onClosed, this);
			LOGGER.info("RelayLink [{}:{}] 转发链接关闭 ", this, this.status);
			this.onClosed();
		}
	}

	protected void onClosed() {
	}

	@Override
	public void closeTunnel(RelayTunnel<?> tunnel) {
		this.write(TunnelDisconnectPacket.FACTORY, new TunnelVoidArguments(tunnel));
	}

	private int createPacketId() {
		return this.packetIdCreator.incrementAndGet();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BaseRelayLink)) {
			return false;
		}
		BaseRelayLink that = (BaseRelayLink)o;
		return new EqualsBuilder().append(getInstanceId(), that.getInstanceId())
				.append(getId(), that.getId())
				.append(getService(), that.getService())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getId()).append(getService()).append(getInstanceId()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", this.getId())
				.append("link", this.transporter.getLocalAddress())
				.toString();
	}

}

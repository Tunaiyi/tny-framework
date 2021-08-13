package com.tny.game.net.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.packet.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.relay.RelayPipeStatus.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:17 下午
 */
public class GeneralRelayPipe<UID> implements NetRelayPipe<UID> {

	private volatile RelayPipeStatus status = INIT;

	private final RelayTransmitter transmitter;

	private final Map<Long, NetRelayTubule<UID>> tubuleMap = new ConcurrentHashMap<>();

	private final NetBootstrapContext<UID> bootstrapContext;

	public GeneralRelayPipe(RelayTransmitter transmitter, NetBootstrapContext<UID> bootstrapContext) {
		this.transmitter = transmitter;
		this.bootstrapContext = bootstrapContext;
		this.transmitter.bind(this);
		if (this.transmitter.isActive()) {
			this.status = OPEN;
		}
	}

	@Override
	public long getCreateTime() {
		return 0;
	}

	@Override
	public RelayPipeStatus getStatus() {
		return this.status;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return this.transmitter.getRemoteAddress();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return this.transmitter.getLocalAddress();
	}

	@Override
	public NetRelayTubule<UID> getTubule(long tunnelId) {
		return this.tubuleMap.get(tunnelId);
	}

	@Override
	public RelayTubule<UID> connectTubule(long tunnelId, String host, int port) throws PipeClosedException {
		if (!this.status.isCloseStatus()) {
			throw new PipeClosedException(format("pipe {} closed, connectTubule failed", this.getRemoteAddress()));
		}
		NetRelayTubule<UID> tubule = createTubule(tunnelId, new InetSocketAddress(host, port));
		synchronized (this) {
			if (this.status == CLOSED) {
				throw new PipeClosedException(format("pipe {} closed, connectTubule failed", this.getRemoteAddress()));
			}
			if (this.tubuleMap.putIfAbsent(tunnelId, tubule) == null) {
				tubule.open();
				this.transmitter.write(new TubuleConnectedPacket(tubule.getId()), null);
			}
		}
		return tubule;
	}

	private NetRelayTubule<UID> createTubule(long tunnelId, InetSocketAddress remoteAddress) {
		RelayTubuleTransporter<UID> tubuleTransporter = new RelayTubuleTransporter<>(this, remoteAddress);
		return new GeneralRelayTubule<>(tunnelId, tubuleTransporter, remoteAddress, this.bootstrapContext);
	}

	@Override
	public NetRelayTubule<UID> closeTubule(long tunnelId) {
		NetRelayTubule<UID> tubule = this.tubuleMap.get(tunnelId);
		if (tubule != null) {
			tubule.close();
		}
		return tubule;
	}

	@Override
	public void destroyTubule(NetRelayTubule<UID> tubule) {
		if (this.tubuleMap.remove(tubule.getId(), tubule)) {
			if (!tubule.isClosed()) {
				tubule.close();
			}
			this.transmitter.write(new TubuleDisconnectedPacket(tubule.getId()), null);
		}
	}

	@Override
	public RelayTransmitter getTransmitter() {
		return this.transmitter;
	}

	protected void onClose() {
		RelayTransmitter transporter = this.transmitter;
		if (transporter != null && transporter.isActive()) {
			transporter.close();
		}
	}

	@Override
	public void open() {
		if (this.status != INIT || !this.transmitter.isActive()) {
			return;
		}
		synchronized (this) {
			if (this.status != INIT || !this.transmitter.isActive()) {
				return;
			}
			this.status = OPEN;
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
			this.status = CLOSING;
			this.tubuleMap.forEach((id, tubule) -> tubule.close());
			this.status = CLOSED;
			this.onClose();
		}
	}

	@Override
	public boolean isActive() {
		return this.status == OPEN && this.isConnected();
	}

	private boolean isConnected() {
		RelayTransmitter transporter = this.transmitter;
		return transporter != null && transporter.isActive();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("pipe", this.transmitter.getLocalAddress())
				.toString();
	}

}

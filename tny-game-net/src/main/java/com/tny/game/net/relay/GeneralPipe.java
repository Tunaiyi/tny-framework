package com.tny.game.net.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.packet.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.relay.PipeStatus.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:17 下午
 */
public class GeneralPipe<UID> implements NetPipe<UID> {

	private volatile PipeStatus status = INIT;

	private final RelayTransmitter transmitter;

	private final Map<Long, NetTubule<UID>> tubuleMap = new ConcurrentHashMap<>();

	private final NetBootstrapContext<UID> bootstrapContext;

	public GeneralPipe(RelayTransmitter transmitter, NetBootstrapContext<UID> bootstrapContext) {
		this.transmitter = transmitter;
		this.bootstrapContext = bootstrapContext;
		this.transmitter.bind(this);
		if (this.transmitter.isActive()) {
			this.status = OPEN;
		}
	}

	@Override
	public PipeStatus getStatus() {
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
	public NetTubule<UID> getTubule(long tunnelId) {
		return this.tubuleMap.get(tunnelId);
	}

	@Override
	public Tubule<UID> connectTubule(long tunnelId, String host, int port) throws PipeClosedException {
		if (!this.status.isCloseStatus()) {
			throw new PipeClosedException(format("pipe {} closed, connectTubule failed", this.getRemoteAddress()));
		}
		NetTubule<UID> tubule = createTubule(tunnelId, new InetSocketAddress(host, port));
		synchronized (this) {
			if (this.status == CLOSED) {
				throw new PipeClosedException(format("pipe {} closed, connectTubule failed", this.getRemoteAddress()));
			}
			if (this.tubuleMap.putIfAbsent(tunnelId, tubule) == null) {
				tubule.open();
				this.transmitter.write(new ConnectedPacket(tubule.getId()), null);
			}
		}
		return tubule;
	}

	private NetTubule<UID> createTubule(long tunnelId, InetSocketAddress remoteAddress) {
		TubuleTransporter<UID> tubuleTransporter = new TubuleTransporter<>(this, remoteAddress);
		return new GeneralTubule<>(tunnelId, tubuleTransporter, remoteAddress, this.bootstrapContext);
	}

	@Override
	public NetTubule<UID> closeTubule(long tunnelId) {
		NetTubule<UID> tubule = this.tubuleMap.get(tunnelId);
		if (tubule != null) {
			tubule.close();
		}
		return tubule;
	}

	@Override
	public void destroyTubule(NetTubule<UID> tubule) {
		if (this.tubuleMap.remove(tubule.getId(), tubule)) {
			if (!tubule.isClosed()) {
				tubule.close();
			}
			this.transmitter.write(new DisconnectedPacket(tubule.getId()), null);
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

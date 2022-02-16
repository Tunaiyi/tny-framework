package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.net.InetSocketAddress;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class BaseNetTunnel<UID, E extends NetEndpoint<UID>, T extends MessageTransporter> extends AbstractNetTunnel<UID, E> {

	protected volatile T transporter;

	protected BaseNetTunnel(long id, T transporter, TunnelMode mode, NetworkContext context) {
		super(id, mode, context);
		if (transporter != null) {
			this.transporter = transporter;
			this.transporter.bind(this);
		}
	}

	protected MessageTransporter getTransporter() {
		return this.transporter;
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
		T transporter = this.transporter;
		return this.getStatus() == TunnelStatus.OPEN && transporter != null && transporter.isActive();
	}

	@Override
	public void reset() {
		if (this.status == TunnelStatus.INIT) {
			return;
		}
		synchronized (this) {
			if (this.status == TunnelStatus.INIT) {
				return;
			}
			if (!this.isActive()) {
				this.disconnect();
			}
			this.status = TunnelStatus.INIT;
		}
	}

	@Override
	public MessageWriteAwaiter write(Message message, MessageWriteAwaiter awaiter) throws NetException {
		if (this.checkAvailable(awaiter)) {
			return this.transporter.write(message, awaiter);
		}
		return awaiter;
	}

	@Override
	public MessageWriteAwaiter write(MessageAllocator allocator, MessageContext context) throws NetException {
		MessageWriteAwaiter promise = context.getWriteAwaiter();
		if (this.checkAvailable(promise)) {
			return this.transporter.write(allocator, this.getMessageFactory(), context);
		}
		return promise;
	}

	@Override
	protected void onDisconnect() {
	}

	@Override
	protected void onDisconnected() {
	}

	@Override
	protected void onOpened() {
	}

	@Override
	protected void onClose() {
	}

	@Override
	protected void onClosed() {
	}

	protected void onWriteUnavailable() {
	}

	@Override
	protected void doDisconnect() {
		T transporter = this.transporter;
		if (transporter != null && transporter.isActive()) {
			try {
				transporter.close();
			} catch (Throwable e) {
				LOGGER.error("transporter close error", e);
			}
		}
	}

	private boolean checkAvailable(MessageWriteAwaiter awaiter) {
		if (!this.isActive()) {
			this.onWriteUnavailable();
			if (awaiter != null) {
				awaiter.completeExceptionally(new TunnelDisconnectException("{} is disconnect", this));
			}
			return false;
		}
		return true;
	}

	//	protected AbstractTunnel<UID, E> setNetTransport(T transport) {
	//		this.transporter = transport;
	//		return this;
	//	}

	@Override
	public String toString() {
		return this.getMode() + "[" + this.getUserType() + "(" + this.getUserId() + ") " + this.transporter + "]";
	}

}

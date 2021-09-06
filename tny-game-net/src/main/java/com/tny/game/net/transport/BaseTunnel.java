package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.net.InetSocketAddress;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class BaseTunnel<UID, E extends NetEndpoint<UID>, T extends MessageTransporter<UID>> extends AbstractTunnel<UID, E> {

	protected volatile T transporter;

	protected BaseTunnel(long id, T transporter, TunnelMode mode, NetworkContext context) {
		super(id, mode, context);
		if (transporter != null) {
			this.transporter = transporter;
			this.transporter.bind(this);
		}
	}

	protected MessageTransporter<UID> getTransporter() {
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
	public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
		this.checkAvailable(promise);
		return this.transporter.write(message, promise);
	}

	@Override
	public WriteMessageFuture write(MessageAllocator allocator, MessageContext context) throws NetException {
		WriteMessagePromise promise = as(context.getWriteMessageFuture());
		this.checkAvailable(promise);
		return this.transporter.write(allocator, this.getMessageFactory(), context);
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

	protected void doCloseTransport(T transporter) {
		transporter.close();
	}

	private void checkAvailable(WriteMessagePromise promise) {
		if (!this.isActive()) {
			this.onWriteUnavailable();
			if (promise != null) {
				promise.failedAndThrow(new TunnelDisconnectException("{} is disconnect", this));
			}
		}
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return this.transporter.createWritePromise();
	}

	//	protected AbstractTunnel<UID, E> setNetTransport(T transport) {
	//		this.transporter = transport;
	//		return this;
	//	}

	protected AbstractTunnel<UID, E> setEndpoint(E endpoint) {
		this.endpoint = endpoint;
		return this;
	}

	@Override
	public String toString() {
		return this.getMode() + "[" + this.getUserType() + "(" + this.getUserId() + ") " + this.transporter + "]";
	}

}

package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class BaseClientTunnel<UID, E extends NetTerminal<UID>, T extends Transporter<UID>> extends BaseTunnel<UID, E, T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseClientTunnel.class);

	public BaseClientTunnel(long id, NetBootstrapContext<UID> bootstrapContext) {
		super(id, null, TunnelMode.CLIENT, bootstrapContext);
	}

	@Override
	protected boolean onOpen() {
		if (!this.isActive()) {
			try {
				this.status = TunnelStatus.INIT;
				T transport = as(this.endpoint.connect());
				if (transport != null) {
					this.transporter = transport;
					this.transporter.bind(this);
					this.status = TunnelStatus.OPEN;
					this.endpoint.onConnected(this);
					return true;
				}
			} catch (Exception e) {
				this.disconnect();
				throw new TunnelException(e, "{} failed to connect to server", this);
			}
		}
		LOGGER.warn("{} is available", this);
		return false;
	}

	@Override
	protected void onDisconnect() {
		this.closeTransport();
	}

	@Override
	protected void onWriteUnavailable() {
		this.endpoint.reconnect();
	}

	@Override
	public String toString() {
		return "NettyClientTunnel{" + "channel=" + this.transporter + '}';
	}

	@Override
	protected boolean bindEndpoint(NetEndpoint<UID> endpoint) {
		return this.endpoint == endpoint;
	}

}
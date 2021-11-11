package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class BaseClientTunnel<UID, E extends NetTerminal<UID>, T extends MessageTransporter<UID>> extends BaseTunnel<UID, E, T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseClientTunnel.class);

	public BaseClientTunnel(long id, NetworkContext context) {
		super(id, null, TunnelMode.CLIENT, context);
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
				//				throw new TunnelException(e, "{} failed to connect to server", this);
				LOGGER.warn("{} open failed case : {}", this, e.getMessage());
				return false;
			}
		}
		LOGGER.warn("{} is available", this);
		return true;
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

package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.task.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class BaseServerTunnel<UID, E extends NetSession<UID>, T extends MessageTransporter<UID>> extends BaseTunnel<UID, E, T> {

	public BaseServerTunnel(long id, T transporter, NetBootstrapContext<UID> bootstrapContext) {
		super(id, transporter, TunnelMode.SERVER, bootstrapContext);
	}

	@Override
	protected boolean bindEndpoint(NetEndpoint<UID> endpoint) {
		Certificate<UID> certificate = this.getCertificate();
		if (!certificate.isAuthenticated()) {
			CommandTaskBox commandTaskBox = this.endpoint.getCommandTaskBox();
			this.endpoint = as(endpoint);
			this.endpoint.takeOver(commandTaskBox);
			return true;
		}
		return false;
	}

	@Override
	protected void onDisconnect() {
		this.close();
	}

	@Override
	protected boolean onOpen() {
		T transporter = this.transporter;
		if (transporter == null || !transporter.isActive()) {
			LOGGER.warn("open failed. channel {} is not active", transporter);
			return false;
		}
		return true;
	}

}

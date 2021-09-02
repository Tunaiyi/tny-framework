package com.tny.game.net.endpoint;

import com.tny.game.net.command.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class AnonymityEndpoint<UID> extends AbstractEndpoint<UID> implements NetSession<UID> {

	private static final CommonSessionSetting SETTING = new CommonSessionSetting().setSendMessageCachedSize(0);

	private NetTunnel<UID> tunnel;

	public AnonymityEndpoint(EndpointContext<UID> endpointContext) {
		super(SETTING, endpointContext);
	}

	@Override
	protected NetTunnel<UID> currentTunnel() {
		return this.tunnel;
	}

	@Override
	public void onUnactivated(NetTunnel<UID> tunnel) {
		this.close();
	}

	@Override
	public void heartbeat() {
		this.tunnel.ping();
	}

	@Override
	public Certificate<UID> getCertificate() {
		return this.certificate;
	}

	public AnonymityEndpoint<UID> setTunnel(NetTunnel<UID> tunnel) {
		this.tunnel = tunnel;
		return this;
	}

}

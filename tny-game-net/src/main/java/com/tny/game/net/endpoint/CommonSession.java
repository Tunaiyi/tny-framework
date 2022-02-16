package com.tny.game.net.endpoint;

import com.google.common.base.MoreObjects;
import com.tny.game.net.command.*;
import com.tny.game.net.transport.*;

/**
 * 抽象Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class CommonSession<UID> extends BaseNetEndpoint<UID> implements NetSession<UID> {

	public CommonSession(SessionSetting setting, Certificate<UID> certificate, EndpointContext endpointContext) {
		super(setting, certificate, endpointContext);
	}

	@Override
	public void onUnactivated(NetTunnel<UID> tunnel) {
		if (isOffline()) {
			return;
		}
		synchronized (this) {
			if (isOffline()) {
				return;
			}
			Tunnel<UID> currentTunnel = this.currentTunnel();
			if (currentTunnel.isActive()) {
				return;
			}
			if (isClosed()) {
				return;
			}
			setOffline();
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("userGroup", this.getUserType())
				.add("userId", this.getUserId())
				.add("tunnel", this.currentTunnel())
				.toString();
	}

}

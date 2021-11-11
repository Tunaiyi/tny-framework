package com.tny.game.net.base.configuration;

import com.tny.game.net.base.*;

public class CommonClientBootstrapSetting extends CommonNetBootstrapSetting implements ClientBootstrapSetting {

	private ClientConnectorSetting connector = new ClientConnectorSetting();

	public CommonClientBootstrapSetting() {
	}

	@Override
	public ClientConnectorSetting getConnector() {
		return connector;
	}

	public CommonClientBootstrapSetting setConnector(ClientConnectorSetting connector) {
		this.connector = connector;
		return this;
	}

}

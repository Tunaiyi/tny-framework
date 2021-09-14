package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:09 下午
 */
class NettyRelayLinkConnector implements RelayConnectCallback {

	private String key;

	private final LocalRelayContext relayContext;

	private final NetLocalServeInstance instance;

	private final NettyServeInstanceConnector connector;

	private volatile RelayConnectorStatus status = RelayConnectorStatus.INIT;

	private volatile LocalRelayLink link;

	private int times;

	private final static long[] delayTimeList = {1, 2, 2, 3, 3, 3, 5, 5, 5, 5, 10, 10, 10, 10, 10, 15};

	NettyRelayLinkConnector(LocalRelayContext relayContext, NetLocalServeInstance instance, NettyServeInstanceConnector connector) {
		this.key = relayContext.getCurrentServeName();
		this.relayContext = relayContext;
		this.instance = instance;
		this.connector = connector;
	}

	public URL getUrl() {
		return instance.url();
	}

	public synchronized void connect() {
		if (!status.isCanConnect()) {
			return;
		}
		if (link == null || !link.isActive()) {
			status = RelayConnectorStatus.CONNECTING;
			connector.connect(this);
		}
	}

	private void onConnected(NetRelayTransporter transporter) {
		if (status != RelayConnectorStatus.CONNECTING) {
			transporter.close();
			return;
		}
		this.status = RelayConnectorStatus.OPEN;
		this.times = 0;
		this.key = relayContext.createLinkId();
		this.link = new CommonLocalRelayLink(this.key, instance, transporter);
		this.link.auth(relayContext.getCurrentServeName(), relayContext.getCurrentInstanceId());
		transporter.addCloseListener(this::onClose);
	}

	private void onReconnected() {
		if (status.isCanConnect()) {
			connector.connect(this, delayTimeList[this.times % delayTimeList.length] * 1000);
			this.times++;
		}
	}

	private void onClose(NetRelayTransporter transporter) {
		if (status != RelayConnectorStatus.CLOSE) {
			this.status = RelayConnectorStatus.DISCONNECT;
			onReconnected();
		}
	}

	public synchronized void close() {
		status = RelayConnectorStatus.CLOSE;
	}

	@Override
	public void complete(boolean result, URL url, NetRelayTransporter transporter, Throwable cause) {
		if (this.status == RelayConnectorStatus.CLOSE) {
			NettyLocalServeInstance.LOGGER.warn("Server [{}-{}-{}] Connector is closed",
					relayContext.getCurrentServeName(), relayContext.getCurrentInstanceId(), this.key);
			transporter.close();
			return;
		}
		if (result && transporter.isActive()) {
			NettyLocalServeInstance.LOGGER.info("Server [{}-{}-{}] connect to {} success on the {}th times",
					relayContext.getCurrentServeName(), relayContext.getCurrentInstanceId(), this.key, url, times);
			onConnected(transporter);
		} else {
			this.status = RelayConnectorStatus.DISCONNECT;
			NettyLocalServeInstance.LOGGER.warn("Server [{}-{}-{}] connect to {} failed {} times",
					relayContext.getCurrentServeName(), relayContext.getCurrentInstanceId(), this.key, url, times, cause.getCause());
			onReconnected();
		}
	}

}

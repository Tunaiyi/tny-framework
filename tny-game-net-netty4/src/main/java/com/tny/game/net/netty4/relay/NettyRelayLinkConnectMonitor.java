package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:09 下午
 */
class NettyRelayLinkConnectMonitor implements RelayConnectCallback {

	private final String key;

	private final LocalRelayContext relayContext;

	private final LocalServeInstance instance;

	private final NettyServeInstanceConnector connector;

	private volatile RelayConnectorStatus status = RelayConnectorStatus.INIT;

	private volatile LocalRelayLink link;

	private int times;

	private final static long[] delayTimeList = {1, 2, 2, 3, 3, 3, 5, 5, 5, 5, 10, 10, 10, 10, 10, 15};

	NettyRelayLinkConnectMonitor(String key, LocalRelayContext relayContext, LocalServeInstance instance, NettyServeInstanceConnector connector) {
		this.key = key;
		this.relayContext = relayContext;
		this.instance = instance;
		this.connector = connector;
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

	public URL getUrl() {
		return instance.url();
	}

	private void onConnected(NetRelayTransporter transporter) {
		if (status != RelayConnectorStatus.CONNECTING) {
			transporter.close();
			return;
		}
		this.status = RelayConnectorStatus.OPEN;
		this.times = 0;
		this.link = new CommonLocalRelayLink(this.key, instance, transporter);
		this.link.auth(relayContext.getClusterId(), relayContext.getInstanceId());
		transporter.addOnClose(this::onClose);
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
		if (result) {
			NettyLocalServeInstance.LOGGER.info("Server [{}-{}-{}] connect to {} success on the {}th times",
					relayContext.getClusterId(), relayContext.getInstanceId(), this.key, url, times);
			onConnected(transporter);
		} else {
			this.status = RelayConnectorStatus.DISCONNECT;
			NettyLocalServeInstance.LOGGER.warn("Server [{}-{}-{}] connect to {} failed {} times",
					relayContext.getClusterId(), relayContext.getInstanceId(), this.key, url, times, cause.getCause());
			onReconnected();
		}
	}

}

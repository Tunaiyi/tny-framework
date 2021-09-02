package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.link.*;
import org.apache.commons.lang3.builder.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:09 下午
 */
class NettyRelayLinkConnector implements RelayConnectCallback {

	private final String key;

	private final NettyServeInstance instance;

	private final NettyLocalServeCluster cluster;

	private volatile RelayConnectorStatus status = RelayConnectorStatus.INIT;

	private volatile LocalRelayLink link;

	private int times;

	private final static long[] delayTimeList = {1, 2, 2, 3, 3, 3, 5, 5, 5, 5, 10, 10, 10, 10, 10, 15};

	NettyRelayLinkConnector(String key, NettyServeInstance instance, NettyLocalServeCluster cluster) {
		this.key = key;
		this.instance = instance;
		this.cluster = cluster;
	}

	public String getKey() {
		return key;
	}

	public synchronized void connect() {
		if (status.isCanConnect()) {
			status = RelayConnectorStatus.CONNECTING;
			cluster.connect(instance.getUrl(), this);
		}
	}

	public synchronized void close() {
		status = RelayConnectorStatus.CLOSE;
		if (this.link != null) {
			this.link.close();
		}
	}

	private void onConnected(NetRelayTransporter transporter) {
		if (status != RelayConnectorStatus.CONNECTING) {
			transporter.close();
			return;
		}
		this.status = RelayConnectorStatus.OPEN;
		this.times = 0;
		if (this.link == null) {
			this.link = new CommonLocalRelayLink(instance, this.key, transporter);
			this.link.auth(cluster.getLocalClusterId(), cluster.getLocalInstanceId());
		}
	}

	private void onReconnected() {
		if (status.isCanConnect()) {
			this.status = RelayConnectorStatus.FAILURE;
			cluster.connect(instance.getUrl(), delayTimeList[this.times % delayTimeList.length], this);
			this.times++;
		}
	}

	@Override
	public void complete(boolean result, URL url, NetRelayTransporter transporter, Throwable cause) {
		if (result) {
			NettyServeInstance.LOGGER.info("Server [{}-{}-{}] connect to {} success on the {}th times",
					cluster.getLocalClusterId(), cluster.getLocalInstanceId(), this.getKey(), url, times);
			onConnected(transporter);
		} else {
			NettyServeInstance.LOGGER.warn("Server [{}-{}-{}] connect to {} failed {} times",
					cluster.getLocalClusterId(), cluster.getLocalInstanceId(), this.getKey(), url, times, cause.getCause());
			onReconnected();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof NettyRelayLinkConnector)) {
			return false;
		}
		NettyRelayLinkConnector task = (NettyRelayLinkConnector)o;
		return new EqualsBuilder().append(getKey(), task.getKey()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getKey()).toHashCode();
	}

}

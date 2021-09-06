package com.tny.game.net.netty4.relay.router;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/6 9:12 下午
 */
@ConfigurationProperties("tny.net.relay.router.fixed-message-router")
public class FixedRelayMessageRoutersProperties {

	/**
	 * name : clusterId
	 */
	private String clusterId;

	public String getClusterId() {
		return clusterId;
	}

	public FixedRelayMessageRoutersProperties setClusterId(String clusterId) {
		this.clusterId = clusterId;
		return this;
	}

}

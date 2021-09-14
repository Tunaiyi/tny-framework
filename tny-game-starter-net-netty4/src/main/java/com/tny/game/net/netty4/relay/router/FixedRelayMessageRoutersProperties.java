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
	 * name : serveName
	 */
	private String serveName;

	public String getServeName() {
		return serveName;
	}

	public FixedRelayMessageRoutersProperties setServeName(String serveName) {
		this.serveName = serveName;
		return this;
	}

}

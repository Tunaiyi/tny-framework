package com.tny.game.net.netty4.configuration.channel;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 2:43 上午
 */
@ConfigurationProperties("tny.net.pipeline.read-idle-pipeline-chain")
public class ReadIdlePipelineChainProperties {

	private long idleTimeout = 180000;

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public ReadIdlePipelineChainProperties setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
		return this;
	}

}

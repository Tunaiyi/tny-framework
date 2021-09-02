package com.tny.game.net.netty4.channel;

import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ReadIdlePipelineChain<C extends Channel> implements ChannelPipelineChain {

	private long idleTimeout = 180000;

	public ReadIdlePipelineChain() {
	}

	public ReadIdlePipelineChain(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	@Override
	public void afterMake(ChannelPipeline channelPipeline) {
		channelPipeline.addLast(new ReadTimeoutHandler(this.idleTimeout, TimeUnit.MILLISECONDS));
	}

	public long getIdleTimeout() {
		return this.idleTimeout;
	}

	public ReadIdlePipelineChain<C> setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
		return this;
	}

}

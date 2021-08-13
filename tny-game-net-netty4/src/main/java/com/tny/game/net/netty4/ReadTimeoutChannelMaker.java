package com.tny.game.net.netty4;

import com.tny.game.net.netty4.codec.*;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ReadTimeoutChannelMaker<C extends Channel> extends ChannelMaker<C> {

	private long idleTimeout = 180000;

	public ReadTimeoutChannelMaker() {
	}

	public ReadTimeoutChannelMaker(NetPackEncoder encoder, NetPackDecoder decoder, long idleTimeout) {
		super(encoder, decoder);
		this.idleTimeout = idleTimeout;
	}

	@Override
	protected void prepareAddCoder(ChannelPipeline channelPipeline) {
		channelPipeline.addLast(new ReadTimeoutHandler(this.idleTimeout, TimeUnit.MILLISECONDS));
	}

	public long getIdleTimeout() {
		return this.idleTimeout;
	}

	public ReadTimeoutChannelMaker<C> setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
		return this;
	}

}

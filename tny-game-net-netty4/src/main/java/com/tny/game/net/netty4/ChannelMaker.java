package com.tny.game.net.netty4;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.netty4.codec.*;
import io.netty.channel.*;

@UnitInterface
public abstract class ChannelMaker<C extends Channel> {

	private NetPackEncoder encoder;

	private NetPackDecoder decoder;

	protected ChannelMaker() {
	}

	public ChannelMaker(NetPackEncoder encoder, NetPackDecoder decoder) {
		super();
		this.encoder = encoder;
		this.decoder = decoder;
	}

	public void initChannel(C channel) throws Exception {
		ChannelPipeline channelPipeline = channel.pipeline();
		this.prepareAddCoder(channelPipeline);
		channelPipeline.addLast("frameDecoder", new NetDecoderHandler(this.decoder));
		channelPipeline.addLast("encoder", new NetEncodeHandler(this.encoder));
		this.postAddCoder(channelPipeline);
		this.postInitChannel(channel);
	}

	protected void postAddCoder(ChannelPipeline channelPipeline) {
	}

	protected void prepareAddCoder(ChannelPipeline channelPipeline) {
	}

	protected void postInitChannel(C channel) {
	}

	public ChannelMaker<C> setEncoder(NetPackEncoder encoder) {
		this.encoder = encoder;
		return this;
	}

	public ChannelMaker<C> setDecoder(NetPackDecoder decoder) {
		this.decoder = decoder;
		return this;
	}

}

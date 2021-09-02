package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.netty4.channel.*;
import io.netty.channel.*;

public abstract class RelayPackChannelMaker<C extends Channel> extends BaseChannelMaker<C> {

	private RelayPackEncoder encoder;

	private boolean closeOnEncodeError;

	private RelayPackDecoder decoder;

	private boolean closeOnDecodeError;

	protected RelayPackChannelMaker() {
	}

	public RelayPackChannelMaker(RelayPackEncoder encoder, RelayPackDecoder decoder) {
		super();
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	public void makeChannel(C channel) {
		ChannelPipeline channelPipeline = channel.pipeline();
		channelPipeline.addLast("frameDecoder", new RelayPackDecodeHandler(this.decoder, closeOnDecodeError));
		channelPipeline.addLast("encoder", new RelayPackEncodeHandler(this.encoder, closeOnEncodeError));
	}

	public RelayPackChannelMaker<C> setEncoder(RelayPackEncoder encoder) {
		this.encoder = encoder;
		return this;
	}

	public RelayPackChannelMaker<C> setDecoder(RelayPackDecoder decoder) {
		this.decoder = decoder;
		return this;
	}

	public RelayPackChannelMaker<C> setCloseOnEncodeError(boolean closeOnEncodeError) {
		this.closeOnEncodeError = closeOnEncodeError;
		return this;
	}

	public RelayPackChannelMaker<C> setCloseOnDecodeError(boolean closeOnDecodeError) {
		this.closeOnDecodeError = closeOnDecodeError;
		return this;
	}

}

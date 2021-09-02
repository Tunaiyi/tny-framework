package com.tny.game.net.netty4.datagram.codec;

import com.tny.game.net.netty4.channel.*;
import io.netty.channel.*;

public abstract class DatagramChannelMaker<C extends Channel> extends BaseChannelMaker<C> {

	private DatagramPackEncoder encoder;

	private boolean closeOnEncodeError;

	private DatagramPackDecoder decoder;

	private boolean closeOnDecodeError;

	protected DatagramChannelMaker() {
	}

	public DatagramChannelMaker(DatagramPackEncoder encoder, DatagramPackDecoder decoder) {
		super();
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	protected void makeChannel(C channel) {
		ChannelPipeline channelPipeline = channel.pipeline();
		channelPipeline.addLast("frameDecoder", new DatagramPackDecodeHandler(this.decoder, closeOnDecodeError));
		channelPipeline.addLast("encoder", new DatagramPackEncodeHandler(this.encoder, closeOnEncodeError));
	}

	@Override
	protected void postInitChannel(C channel) {
	}

	public DatagramChannelMaker<C> setEncoder(DatagramPackEncoder encoder) {
		this.encoder = encoder;
		return this;
	}

	public DatagramChannelMaker<C> setDecoder(DatagramPackDecoder decoder) {
		this.decoder = decoder;
		return this;
	}

	public DatagramChannelMaker<C> setCloseOnEncodeError(boolean closeOnEncodeError) {
		this.closeOnEncodeError = closeOnEncodeError;
		return this;
	}

	public DatagramChannelMaker<C> setCloseOnDecodeError(boolean closeOnDecodeError) {
		this.closeOnDecodeError = closeOnDecodeError;
		return this;
	}

}

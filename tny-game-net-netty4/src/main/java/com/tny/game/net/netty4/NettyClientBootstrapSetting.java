package com.tny.game.net.netty4;

import com.tny.game.net.base.configuration.*;
import com.tny.game.net.netty4.codec.*;

public class NettyClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyBootstrapSetting {

	private DataPacketCodecSetting encoder = new DataPacketCodecSetting();

	private DataPacketCodecSetting decoder = new DataPacketCodecSetting();

	private NettyChannelMakerSetting channelMaker = new NettyChannelMakerSetting();

	public NettyClientBootstrapSetting() {
	}

	public NettyClientBootstrapSetting(String name) {
		this.setName(name);
	}

	public NettyChannelMakerSetting getChannelMaker() {
		return this.channelMaker;
	}

	public NettyClientBootstrapSetting setChannelMaker(NettyChannelMakerSetting channelMaker) {
		this.channelMaker = channelMaker;
		return this;
	}

	@Override
	public DataPacketCodecSetting getEncoder() {
		return encoder;
	}

	@Override
	public DataPacketCodecSetting getDecoder() {
		return decoder;
	}

	public NettyClientBootstrapSetting setEncoder(DataPacketCodecSetting encoder) {
		this.encoder = encoder;
		return this;
	}

	public NettyClientBootstrapSetting setDecoder(DataPacketCodecSetting decoder) {
		this.decoder = decoder;
		return this;
	}

}

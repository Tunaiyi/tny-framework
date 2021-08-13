package com.tny.game.net.netty4;

import com.tny.game.net.base.configuration.*;
import com.tny.game.net.netty4.codec.*;

public class NettyServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyBootstrapSetting {

	private NettyChannelMakerSetting channelMaker = new NettyChannelMakerSetting();

	private DataPacketCodecSetting encoder = new DataPacketCodecSetting();

	private DataPacketCodecSetting decoder = new DataPacketCodecSetting();

	public NettyServerBootstrapSetting() {
	}

	public NettyServerBootstrapSetting(String name) {
		this.setName(name);
	}

	public NettyChannelMakerSetting getChannelMaker() {
		return this.channelMaker;
	}

	@Override
	public DataPacketCodecSetting getEncoder() {
		return encoder;
	}

	@Override
	public DataPacketCodecSetting getDecoder() {
		return decoder;
	}

	public NettyServerBootstrapSetting setChannelMaker(NettyChannelMakerSetting channelMaker) {
		this.channelMaker = channelMaker;
		return this;
	}

	public NettyServerBootstrapSetting setEncoder(DataPacketCodecSetting encoder) {
		this.encoder = encoder;
		return this;
	}

	public NettyServerBootstrapSetting setDecoder(DataPacketCodecSetting decoder) {
		this.decoder = decoder;
		return this;
	}

}

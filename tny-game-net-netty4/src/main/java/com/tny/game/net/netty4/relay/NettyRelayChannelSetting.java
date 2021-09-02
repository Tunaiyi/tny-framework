package com.tny.game.net.netty4.relay;

import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.relay.codec.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 2:54 下午
 */
public class NettyRelayChannelSetting {

	private NettyChannelMakerSetting maker = new NettyChannelMakerSetting(DefaultRelayChannelMaker.class);

	private RelayPacketCodecSetting encoder = new RelayPacketCodecSetting(false);

	private RelayPacketCodecSetting decoder = new RelayPacketCodecSetting(true);

	public NettyChannelMakerSetting getMaker() {
		return maker;
	}

	public RelayPacketCodecSetting getEncoder() {
		return encoder;
	}

	public RelayPacketCodecSetting getDecoder() {
		return decoder;
	}

	public NettyRelayChannelSetting setMaker(NettyChannelMakerSetting maker) {
		this.maker = maker;
		return this;
	}

	public NettyRelayChannelSetting setEncoder(RelayPacketCodecSetting encoder) {
		this.encoder = encoder;
		return this;
	}

	public NettyRelayChannelSetting setDecoder(RelayPacketCodecSetting decoder) {
		this.decoder = decoder;
		return this;
	}

}

package com.tny.game.net.netty4;

import com.tny.game.net.netty4.codec.*;

import static com.tny.game.net.base.configuration.NetUnitUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 7:41 下午
 */
public class NettyChannelSetting {

	private NettyChannelMakerSetting maker;

	private DataPacketCodecSetting encoder;

	private DataPacketCodecSetting decoder;

	private String handler = defaultName(NettyMessageHandler.class);

	private String tunnelFactory = defaultName(NettyTunnelFactory.class);

	public NettyChannelSetting() {
		this.maker = new NettyChannelMakerSetting();
		this.encoder = new DataPacketCodecSetting();
		this.decoder = new DataPacketCodecSetting();
	}

	public NettyChannelSetting(NettyChannelMakerSetting maker, DataPacketCodecSetting encoder, DataPacketCodecSetting decoder) {
		this.maker = maker;
		this.encoder = encoder;
		this.decoder = decoder;
	}

	public NettyChannelMakerSetting getMaker() {
		return this.maker;
	}

	public DataPacketCodecSetting getEncoder() {
		return encoder;
	}

	public DataPacketCodecSetting getDecoder() {
		return decoder;
	}

	public String getHandler() {
		return handler;
	}

	public String getTunnelFactory() {
		return tunnelFactory;
	}

	public NettyChannelSetting setMaker(NettyChannelMakerSetting maker) {
		this.maker = maker;
		return this;
	}

	public NettyChannelSetting setEncoder(DataPacketCodecSetting encoder) {
		this.encoder = encoder;
		return this;
	}

	public NettyChannelSetting setDecoder(DataPacketCodecSetting decoder) {
		this.decoder = decoder;
		return this;
	}

	public NettyChannelSetting setHandler(String handler) {
		this.handler = handler;
		return this;
	}

	public NettyChannelSetting setTunnelFactory(String tunnelFactory) {
		this.tunnelFactory = tunnelFactory;
		return this;
	}

}

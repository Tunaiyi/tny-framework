package com.tny.game.net.netty4.datagram;

import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.datagram.codec.*;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 7:41 下午
 */
public class NettyDatagramChannelSetting {

	private NettyChannelMakerSetting maker;

	private DatagramPackCodecSetting encoder;

	private DatagramPackCodecSetting decoder;

	private String messageHandler = defaultName(NettyMessageHandler.class);

	private String tunnelFactory = defaultName(NettyTunnelFactory.class);

	public NettyDatagramChannelSetting(String encodeBodyCodec, String decodeBodyCodec) {
		this.maker = new NettyChannelMakerSetting(DefaultDatagramChannelMaker.class);
		this.encoder = new DatagramPackCodecSetting()
				.setBodyCodec(encodeBodyCodec)
				.setCloseOnError(false);
		this.decoder = new DatagramPackCodecSetting()
				.setBodyCodec(decodeBodyCodec)
				.setCloseOnError(true);
	}

	public NettyChannelMakerSetting getMaker() {
		return this.maker;
	}

	public DatagramPackCodecSetting getEncoder() {
		return encoder;
	}

	public DatagramPackCodecSetting getDecoder() {
		return decoder;
	}

	public String getMessageHandler() {
		return messageHandler;
	}

	public String getTunnelFactory() {
		return tunnelFactory;
	}

	public NettyDatagramChannelSetting setMaker(NettyChannelMakerSetting maker) {
		this.maker = maker;
		return this;
	}

	public NettyDatagramChannelSetting setEncoder(DatagramPackCodecSetting encoder) {
		this.encoder = encoder;
		return this;
	}

	public NettyDatagramChannelSetting setDecoder(DatagramPackCodecSetting decoder) {
		this.decoder = decoder;
		return this;
	}

	public NettyDatagramChannelSetting setMessageHandler(String messageHandler) {
		this.messageHandler = messageHandler;
		return this;
	}

	public NettyDatagramChannelSetting setTunnelFactory(String tunnelFactory) {
		this.tunnelFactory = tunnelFactory;
		return this;
	}

}

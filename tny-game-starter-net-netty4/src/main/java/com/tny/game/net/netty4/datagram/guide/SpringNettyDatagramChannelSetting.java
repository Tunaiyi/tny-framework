package com.tny.game.net.netty4.datagram.guide;

import com.tny.game.net.message.codec.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.datagram.*;
import com.tny.game.net.netty4.datagram.codec.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

public class SpringNettyDatagramChannelSetting extends NettyDatagramChannelSetting {

	@NestedConfigurationProperty
	private NettyChannelMakerSetting maker;

	@NestedConfigurationProperty
	private DatagramPackCodecSetting encoder;

	@NestedConfigurationProperty
	private DatagramPackCodecSetting decoder;

	public SpringNettyDatagramChannelSetting() {
		super(lowerCamelName(ProtoExMessageBodyCodec.class), lowerCamelName(ProtoExMessageBodyCodec.class));
	}

	@Override
	public NettyChannelMakerSetting getMaker() {
		return super.getMaker();
	}

	@Override
	public DatagramPackCodecSetting getEncoder() {
		return super.getEncoder();
	}

	@Override
	public DatagramPackCodecSetting getDecoder() {
		return super.getDecoder();
	}

	@Override
	public NettyDatagramChannelSetting setMaker(NettyChannelMakerSetting maker) {
		return super.setMaker(maker);
	}

	@Override
	public NettyDatagramChannelSetting setEncoder(DatagramPackCodecSetting encoder) {
		return super.setEncoder(encoder);
	}

	@Override
	public NettyDatagramChannelSetting setDecoder(DatagramPackCodecSetting decoder) {
		return super.setDecoder(decoder);
	}

}

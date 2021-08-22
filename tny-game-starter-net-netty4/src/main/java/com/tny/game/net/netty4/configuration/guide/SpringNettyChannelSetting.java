package com.tny.game.net.netty4.configuration.guide;

import com.tny.game.net.netty4.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

public class SpringNettyChannelSetting extends NettyChannelSetting {

	@NestedConfigurationProperty
	private SpringNettyChannelMakerSetting maker;

	@NestedConfigurationProperty
	private SpringDataPacketCodecSetting encoder;

	@NestedConfigurationProperty
	private SpringDataPacketCodecSetting decoder;

	public SpringNettyChannelSetting() {
		super(new SpringNettyChannelMakerSetting(), new SpringDataPacketCodecSetting(), new SpringDataPacketCodecSetting());
	}

	@Override
	public SpringNettyChannelMakerSetting getMaker() {
		return as(super.getMaker());
	}

	@Override
	public SpringDataPacketCodecSetting getEncoder() {
		return as(super.getEncoder());
	}

	@Override
	public SpringDataPacketCodecSetting getDecoder() {
		return as(super.getDecoder());
	}

	public SpringNettyChannelSetting setMaker(SpringNettyChannelMakerSetting maker) {
		super.setMaker(maker);
		return this;
	}

	public SpringNettyChannelSetting setEncoder(SpringDataPacketCodecSetting encoder) {
		super.setEncoder(encoder);
		return this;
	}

	public SpringNettyChannelSetting setDecoder(SpringDataPacketCodecSetting decoder) {
		super.setDecoder(decoder);
		return this;
	}

}

package com.tny.game.net.netty4.relay.guide;

import com.tny.game.net.message.codec.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.netty4.relay.codec.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 4:12 下午
 */
public class SpringNettyRelayChannelSetting extends NettyRelayChannelSetting {

	@NestedConfigurationProperty
	private NettyChannelMakerSetting maker;

	@NestedConfigurationProperty
	private RelayPacketCodecSetting encoder;

	@NestedConfigurationProperty
	private RelayPacketCodecSetting decoder;

	public SpringNettyRelayChannelSetting() {
		this.setMaker(new NettyChannelMakerSetting(DefaultRelayChannelMaker.class))
				.setEncoder(new RelayPacketCodecSetting()
						.setBodyCodec(lowerCamelName(ProtoExMessageBodyCodec.class))
						.setCloseOnError(false))
				.setDecoder(new RelayPacketCodecSetting()
						.setBodyCodec(lowerCamelName(ProtoExMessageBodyCodec.class))
						.setCloseOnError(true));
	}

	@Override
	public NettyChannelMakerSetting getMaker() {
		return as(super.getMaker());
	}

	@Override
	public NettyRelayChannelSetting setMaker(NettyChannelMakerSetting maker) {
		super.setMaker(maker);
		return this;
	}

	@Override
	public RelayPacketCodecSetting getEncoder() {
		return super.getEncoder();
	}

	@Override
	public NettyRelayChannelSetting setEncoder(RelayPacketCodecSetting encoder) {
		return super.setEncoder(encoder);
	}

	@Override
	public RelayPacketCodecSetting getDecoder() {
		return super.getDecoder();
	}

	@Override
	public NettyRelayChannelSetting setDecoder(RelayPacketCodecSetting decoder) {
		return super.setDecoder(decoder);
	}

}

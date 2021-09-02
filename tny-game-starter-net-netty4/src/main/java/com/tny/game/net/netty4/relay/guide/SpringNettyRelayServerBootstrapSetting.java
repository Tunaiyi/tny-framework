package com.tny.game.net.netty4.relay.guide;

import com.tny.game.net.netty4.relay.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 3:56 下午
 */
public class SpringNettyRelayServerBootstrapSetting extends NettyRelayServerBootstrapSetting {

	@NestedConfigurationProperty
	private SpringNettyRelayChannelSetting channel;

	public SpringNettyRelayServerBootstrapSetting() {
		super(new SpringNettyRelayChannelSetting());
	}

	@Override
	public SpringNettyRelayChannelSetting getChannel() {
		return (SpringNettyRelayChannelSetting)super.getChannel();
	}

	public NettyRelayServerBootstrapSetting setChannel(SpringNettyRelayChannelSetting channel) {
		return super.setChannel(channel);
	}

}


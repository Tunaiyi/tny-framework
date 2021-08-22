package com.tny.game.net.netty4.configuration.guide;

import com.tny.game.net.netty4.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 只是为了生成配置说明
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
//@Order(HIGHEST_PRECEDENCE)
//@Configuration
public class SpringNettyClientBootstrapSetting extends NettyClientBootstrapSetting {

	@NestedConfigurationProperty
	private SpringNettyChannelSetting channel;

	public SpringNettyClientBootstrapSetting() {
		super(new SpringNettyChannelSetting());
	}

	@Override
	public SpringNettyChannelSetting getChannel() {
		return as(super.getChannel());
	}

	public SpringNettyClientBootstrapSetting setChannel(SpringNettyChannelSetting channel) {
		super.setChannel(channel);
		return this;
	}

}

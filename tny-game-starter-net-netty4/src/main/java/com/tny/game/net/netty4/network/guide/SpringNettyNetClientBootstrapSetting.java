package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.netty4.network.*;
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
public class SpringNettyNetClientBootstrapSetting extends NettyNetClientBootstrapSetting {

	@NestedConfigurationProperty
	private SpringNettyDatagramChannelSetting channel;

	public SpringNettyNetClientBootstrapSetting() {
		super(new SpringNettyDatagramChannelSetting());
	}

	@Override
	public SpringNettyDatagramChannelSetting getChannel() {
		return as(super.getChannel());
	}

	public NettyNetClientBootstrapSetting setChannel(SpringNettyDatagramChannelSetting channel) {
		return super.setChannel(channel);
	}

}

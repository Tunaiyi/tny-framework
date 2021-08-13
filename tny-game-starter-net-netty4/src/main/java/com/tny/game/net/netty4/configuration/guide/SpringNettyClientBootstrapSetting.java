package com.tny.game.net.netty4.configuration.guide;

import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.codec.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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
	private DataPacketCodecSetting encoder;

	@NestedConfigurationProperty
	private DataPacketCodecSetting decoder;

	@NestedConfigurationProperty
	private NettyChannelMakerSetting channelMaker;

}

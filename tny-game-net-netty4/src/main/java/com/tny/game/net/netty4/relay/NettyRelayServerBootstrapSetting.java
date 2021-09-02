package com.tny.game.net.netty4.relay;

import com.tny.game.net.base.configuration.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 1:50 下午
 */
public class NettyRelayServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyRelayBootstrapSetting {

	private NettyRelayChannelSetting channel;

	public NettyRelayServerBootstrapSetting() {
	}

	public NettyRelayServerBootstrapSetting(NettyRelayChannelSetting channel) {
		this.channel = channel;
	}

	@Override
	public NettyRelayChannelSetting getChannel() {
		return channel;
	}

	public NettyRelayServerBootstrapSetting setChannel(NettyRelayChannelSetting channel) {
		this.channel = channel;
		return this;
	}

}

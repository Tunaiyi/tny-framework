package com.tny.game.net.netty4;

import com.tny.game.net.base.configuration.*;

public class NettyClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyBootstrapSetting {

	private NettyChannelSetting channel;

	public NettyClientBootstrapSetting() {
		this.channel = new NettyChannelSetting();
	}

	public NettyClientBootstrapSetting(NettyChannelSetting channel) {
		this.channel = channel;
	}

	public NettyClientBootstrapSetting(String name) {
		this.setName(name);
	}

	@Override
	public NettyChannelSetting getChannel() {
		return channel;
	}

	public NettyClientBootstrapSetting setChannel(NettyChannelSetting channel) {
		this.channel = channel;
		return this;
	}

}

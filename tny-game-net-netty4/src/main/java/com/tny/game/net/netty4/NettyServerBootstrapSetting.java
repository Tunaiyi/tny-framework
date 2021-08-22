package com.tny.game.net.netty4;

import com.tny.game.net.base.configuration.*;

public class NettyServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyBootstrapSetting {

	private NettyChannelSetting channel;

	public NettyServerBootstrapSetting() {
		channel = new NettyChannelSetting();
	}

	public NettyServerBootstrapSetting(NettyChannelSetting channel) {
		this.channel = channel;
	}

	public NettyServerBootstrapSetting(String name) {
		this.setName(name);
	}

	@Override
	public NettyChannelSetting getChannel() {
		return channel;
	}

	public NettyServerBootstrapSetting setChannel(NettyChannelSetting channel) {
		this.channel = channel;
		return this;
	}

}

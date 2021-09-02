package com.tny.game.net.netty4.datagram;

import com.tny.game.net.base.configuration.*;
import com.tny.game.net.netty4.*;

public class NettyNetClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyBootstrapSetting {

	private NettyDatagramChannelSetting channel;

	public NettyNetClientBootstrapSetting() {
		this.channel = new NettyDatagramChannelSetting(null, null);
	}

	public NettyNetClientBootstrapSetting(NettyDatagramChannelSetting channel) {
		this.channel = channel;
	}

	public NettyNetClientBootstrapSetting(String name) {
		this.setName(name);
	}

	@Override
	public NettyDatagramChannelSetting getChannel() {
		return channel;
	}

	public NettyNetClientBootstrapSetting setChannel(NettyDatagramChannelSetting channel) {
		this.channel = channel;
		return this;
	}

}

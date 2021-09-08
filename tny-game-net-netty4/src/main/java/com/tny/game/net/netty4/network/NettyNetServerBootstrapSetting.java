package com.tny.game.net.netty4.network;

import com.tny.game.net.base.configuration.*;
import com.tny.game.net.netty4.*;

public class NettyNetServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyBootstrapSetting {

	private NettyDatagramChannelSetting channel;

	public NettyNetServerBootstrapSetting() {
		this(null, null);
	}

	public NettyNetServerBootstrapSetting(String encodeBodyCodec, String decodeBodyCodec) {
		channel = new NettyDatagramChannelSetting(encodeBodyCodec, decodeBodyCodec);
	}

	public NettyNetServerBootstrapSetting(NettyDatagramChannelSetting channel) {
		this.channel = channel;
	}

	public NettyNetServerBootstrapSetting(String name) {
		this.setName(name);
	}

	@Override
	public NettyDatagramChannelSetting getChannel() {
		return channel;
	}

	public NettyNetServerBootstrapSetting setChannel(NettyDatagramChannelSetting channel) {
		this.channel = channel;
		return this;
	}

}

package com.tny.game.net.netty4.network;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 7:25 下午
 */
@Unit
public class ServerTunnelFactory implements NettyTunnelFactory {

	@Override
	public <T> NetTunnel<T> create(long id, Channel channel, NetworkContext context) {
		MessageTransporter<T> transport = new NettyChannelMessageTransporter<>(channel);
		return new GeneralServerTunnel<>(id, transport, context); // 创建 Tunnel 已经transport.bind
	}

}

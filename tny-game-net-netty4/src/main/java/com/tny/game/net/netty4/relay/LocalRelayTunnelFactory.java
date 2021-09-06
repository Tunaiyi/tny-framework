package com.tny.game.net.netty4.relay;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.datagram.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 3:28 下午
 */
public class LocalRelayTunnelFactory implements NettyTunnelFactory {

	private final LocalRelayExplorer localRelayExplorer;

	public LocalRelayTunnelFactory(LocalRelayExplorer localRelayExplorer) {
		this.localRelayExplorer = localRelayExplorer;
	}

	@Override
	public <T> NetTunnel<T> create(long id, Channel channel, NetworkContext context) {
		MessageTransporter<T> transport = new NettyChannelMessageTransporter<>(channel);
		DoneResult<LocalRelayTunnel<T>> result = localRelayExplorer.createTunnel(id, transport, context);
		if (result.isFailure()) {
			throw new NetGeneralException(result.getCode());
		}
		return result.get();
	}

}

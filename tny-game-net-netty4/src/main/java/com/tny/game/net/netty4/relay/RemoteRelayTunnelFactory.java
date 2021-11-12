package com.tny.game.net.netty4.relay;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 3:28 下午
 */
public class RemoteRelayTunnelFactory implements NettyTunnelFactory {

	private final RemoteRelayExplorer remoteRelayExplorer;

	public RemoteRelayTunnelFactory(RemoteRelayExplorer remoteRelayExplorer) {
		this.remoteRelayExplorer = remoteRelayExplorer;
	}

	@Override
	public <T> NetTunnel<T> create(long id, Channel channel, NetworkContext context) {
		MessageTransporter<T> transport = new NettyChannelMessageTransporter<>(channel);
		DoneResult<RemoteRelayTunnel<T>> result = remoteRelayExplorer.createTunnel(id, transport, context);
		if (result.isFailure()) {
			throw new NetGeneralException(result.getCode());
		}
		return result.get();
	}

}

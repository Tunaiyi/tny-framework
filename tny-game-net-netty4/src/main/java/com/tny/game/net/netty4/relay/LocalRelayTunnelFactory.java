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

	private final NettyTunnelFactory tunnelFactory;

	private final LocalRelayExplorer localRelayExplorer;

	public LocalRelayTunnelFactory(NettyTunnelFactory tunnelFactory, LocalRelayExplorer localRelayExplorer) {
		this.tunnelFactory = tunnelFactory;
		this.localRelayExplorer = localRelayExplorer;
	}

	@Override
	public <T> NetTunnel<T> create(long id, Channel channel, NetworkContext<T> context) {
		NetTunnel<T> tunnel = tunnelFactory.create(id, channel, context);
		DoneResult<LocalRelayTunnel<T>> result = localRelayExplorer.bindTunnel(tunnel);
		if (result.isFailure()) {
			throw new NetGeneralException(result.getCode());
		}
		return result.get();
	}

}

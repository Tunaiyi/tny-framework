package com.tny.game.net.netty4.network;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 7:22 下午
 */
@UnitInterface
public interface NettyTunnelFactory {

	<T> NetTunnel<T> create(long id, Channel channel, NetworkContext context);

}

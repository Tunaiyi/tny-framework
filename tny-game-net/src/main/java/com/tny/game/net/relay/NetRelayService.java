package com.tny.game.net.relay;

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 6:05 下午
 */
public interface NetRelayService<UID> {

	RelayTunnel<UID> loadTunnel(Tunnel<?> tunnel);

	RelayTunnel<UID> getTunnel(Tunnel<?> tunnel);

	void unload(Tunnel<?> tunnel);

	RelayLink loadLink(Tunnel<?> tunnel);

	ResultCode relay(NetTunnel<?> tunnel, Message message);

}

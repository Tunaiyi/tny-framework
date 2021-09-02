package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 4:09 下午
 */
@FunctionalInterface
public interface RelayConnectCallback {

	void complete(boolean result, URL url, NetRelayTransporter transporter, Throwable cause);

}

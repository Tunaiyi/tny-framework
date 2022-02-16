package com.tny.game.net.relay.link;

import com.tny.game.common.url.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 4:09 下午
 */
@FunctionalInterface
public interface RelayConnectCallback {

	void complete(boolean result, URL url, RelayTransporter transporter, Throwable cause);

}

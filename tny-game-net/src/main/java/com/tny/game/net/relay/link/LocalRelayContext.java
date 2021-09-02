package com.tny.game.net.relay.link;

import com.tny.game.net.relay.link.route.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 2:47 下午
 */
public interface LocalRelayContext {

	String getClusterId();

	long getInstanceId();

	String createId();

	RelayMessageRouter getRelayMessageRouter();

}

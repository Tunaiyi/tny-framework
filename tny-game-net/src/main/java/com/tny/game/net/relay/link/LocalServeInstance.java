package com.tny.game.net.relay.link;

import com.tny.game.net.relay.cluster.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:24 下午
 */
public interface LocalServeInstance extends ServeInstance {

	List<LocalRelayLink> getRelayLinks();

	void register(LocalRelayLink link);

	void relieve(LocalRelayLink link);

	void close();

	void heartbeat();

}

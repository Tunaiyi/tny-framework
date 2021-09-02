package com.tny.game.net.relay.link.route;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 5:34 下午
 */
public class AllRequiredLocalServeClusterSelector implements LocalServeClusterSelector {

	@Override
	public ServeClusterImportance route(Tunnel<?> tunnel, LocalServeCluster cluster) {
		return ServeClusterImportance.REQUIRED;
	}

}

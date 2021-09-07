package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.cluster.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 7:36 下午
 */
public interface RelayServeClusterSetting {

	String getId();

	int getLinkConnectionSize();

	List<ServeNode> getNodes();

	long getLinkHeartbeatInterval();

	long getLinkMaxIdleTime();

}
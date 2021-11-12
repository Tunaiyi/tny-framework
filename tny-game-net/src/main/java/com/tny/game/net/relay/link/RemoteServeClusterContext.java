package com.tny.game.net.relay.link;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.allot.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/3 8:47 下午
 */
public interface RemoteServeClusterContext extends RemoteServeClusterSetting {

	List<NetAccessPoint> getInstances();

	ServeInstanceAllotStrategy getServeInstanceAllotStrategy();

	RelayLinkAllotStrategy getRelayLinkAllotStrategy();

	/**
	 * 连接 link
	 *
	 * @param url      连接服务器 url
	 * @param callback 回调
	 */
	void connect(URL url, RelayConnectCallback callback);

}

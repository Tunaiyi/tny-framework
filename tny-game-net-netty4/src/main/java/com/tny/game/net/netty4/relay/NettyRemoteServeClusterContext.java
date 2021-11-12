package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;

import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public class NettyRemoteServeClusterContext implements RemoteServeClusterContext {

	private final RelayServeClusterSetting setting;

	private RelayClientGuide clientGuide;

	private ServeInstanceAllotStrategy serveInstanceAllotStrategy = new PollingRelayAllotStrategy();

	private RelayLinkAllotStrategy relayLinkAllotStrategy = new PollingRelayAllotStrategy();

	public NettyRemoteServeClusterContext(RelayServeClusterSetting setting) {
		this.setting = setting;
	}

	@Override
	public String getServeName() {
		return setting.getServeName();
	}

	@Override
	public String getUsername() {
		return setting.getUsername();
	}

	public RelayClientGuide getClientGuide() {
		return clientGuide;
	}

	@Override
	public long getLinkMaxIdleTime() {
		return setting.getLinkMaxIdleTime();
	}

	@Override
	public int getLinkConnectionSize() {
		return setting.getLinkConnectionSize();
	}

	@Override
	public boolean isDiscoveryEnable() {
		return setting.isDiscoveryEnable();
	}

	@Override
	public long getLinkHeartbeatInterval() {
		return this.setting.getLinkHeartbeatInterval();
	}

	@Override
	public List<NetAccessPoint> getInstances() {
		return as(setting.getInstanceList());
	}

	@Override
	public ServeInstanceAllotStrategy getServeInstanceAllotStrategy() {
		return serveInstanceAllotStrategy;
	}

	@Override
	public RelayLinkAllotStrategy getRelayLinkAllotStrategy() {
		return relayLinkAllotStrategy;
	}

	/**
	 * @param url url
	 */
	@Override
	public void connect(URL url, RelayConnectCallback callback) {
		clientGuide.connect(url, callback);
	}

	public NettyRemoteServeClusterContext setClientGuide(RelayClientGuide clientGuide) {
		this.clientGuide = clientGuide;
		return this;
	}

	public NettyRemoteServeClusterContext setServeInstanceAllotStrategy(
			ServeInstanceAllotStrategy serveInstanceAllotStrategy) {
		this.serveInstanceAllotStrategy = serveInstanceAllotStrategy;
		return this;
	}

	public NettyRemoteServeClusterContext setRelayLinkAllotStrategy(RelayLinkAllotStrategy relayLinkAllotStrategy) {
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
		return this;
	}

}

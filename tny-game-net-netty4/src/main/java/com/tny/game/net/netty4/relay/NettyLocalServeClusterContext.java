package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public class NettyLocalServeClusterContext implements LocalServeClusterContext {

	private final LocalServeClusterSetting setting;

	private RelayClientGuide clientGuide;

	private LocalServeInstanceAllotStrategy serveInstanceAllotStrategy = new PollingRelayAllotStrategy();

	private LocalRelayLinkAllotStrategy relayLinkAllotStrategy = new PollingRelayAllotStrategy();

	public NettyLocalServeClusterContext(RelayServeClusterSetting setting) {
		this.setting = setting;
	}

	@Override
	public String getServeName() {
		return setting.getServeName();
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
	public LocalServeInstanceAllotStrategy getServeInstanceAllotStrategy() {
		return serveInstanceAllotStrategy;
	}

	@Override
	public LocalRelayLinkAllotStrategy getRelayLinkAllotStrategy() {
		return relayLinkAllotStrategy;
	}

	/**
	 * @param url url
	 */
	@Override
	public void connect(URL url, RelayConnectCallback callback) {
		clientGuide.connect(url, callback);
	}

	public NettyLocalServeClusterContext setClientGuide(RelayClientGuide clientGuide) {
		this.clientGuide = clientGuide;
		return this;
	}

	public NettyLocalServeClusterContext setServeInstanceAllotStrategy(
			LocalServeInstanceAllotStrategy serveInstanceAllotStrategy) {
		this.serveInstanceAllotStrategy = serveInstanceAllotStrategy;
		return this;
	}

	public NettyLocalServeClusterContext setRelayLinkAllotStrategy(LocalRelayLinkAllotStrategy relayLinkAllotStrategy) {
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
		return this;
	}

}

package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public class NettyLocalServeClusterContext implements LocalServeClusterContext {

	private String id;

	private int linkConnectionSize;

	private long linkHeartbeatInterval;

	private long linkMaxIdleTime;

	private List<ServeNode> nodes = new ArrayList<>();

	private RelayClientGuide clientGuide;

	private LocalServeInstanceAllotStrategy serveInstanceAllotStrategy = new PollingRelayAllotStrategy();

	private LocalRelayLinkAllotStrategy relayLinkAllotStrategy = new PollingRelayAllotStrategy();

	public NettyLocalServeClusterContext(RelayServeClusterSetting setting) {
		this.id = setting.getId();
		this.linkMaxIdleTime = setting.getLinkMaxIdleTime();
		this.linkConnectionSize = setting.getLinkConnectionSize();
		this.linkHeartbeatInterval = setting.getLinkHeartbeatInterval();
		this.nodes.addAll(setting.getNodes());
	}

	@Override
	public String getId() {
		return id;
	}

	public RelayClientGuide getClientGuide() {
		return clientGuide;
	}

	@Override
	public long getLinkMaxIdleTime() {
		return linkMaxIdleTime;
	}

	@Override
	public int getLinkConnectionSize() {
		return linkConnectionSize;
	}

	@Override
	public long getLinkHeartbeatInterval() {
		return this.linkHeartbeatInterval;
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

	public Collection<ServeNode> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public NettyLocalServeClusterContext setId(String id) {
		this.id = id;
		return this;
	}

	public NettyLocalServeClusterContext setLinkConnectionSize(int linkConnectionSize) {
		this.linkConnectionSize = linkConnectionSize;
		return this;
	}

	public NettyLocalServeClusterContext setLinkHeartbeatInterval(long linkHeartbeatInterval) {
		this.linkHeartbeatInterval = linkHeartbeatInterval;
		return this;
	}

	public NettyLocalServeClusterContext setLinkMaxIdleTime(long linkMaxIdleTime) {
		this.linkMaxIdleTime = linkMaxIdleTime;
		return this;
	}

	public NettyLocalServeClusterContext setNodes(List<ServeNode> nodes) {
		this.nodes = nodes;
		return this;
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

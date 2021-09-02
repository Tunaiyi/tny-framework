package com.tny.game.net.netty4.relay;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.allot.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public class RelayServeClusterContext {

	private String id;

	private int connectionSize;

	private List<ServeNode> nodes = new ArrayList<>();

	private RelayClientGuide clientGuide;

	private LocalServeInstanceAllotStrategy serveInstanceAllotStrategy = new PollingRelayAllotStrategy();

	private LocalRelayLinkAllotStrategy relayLinkAllotStrategy = new PollingRelayAllotStrategy();

	public RelayServeClusterContext(RelayServeClusterSetting setting) {
		this.id = setting.getId();
		this.connectionSize = setting.getConnectionSize();
		this.nodes.addAll(setting.getNodes());
	}

	public String getId() {
		return id;
	}

	public RelayClientGuide getClientGuide() {
		return clientGuide;
	}

	public LocalServeInstanceAllotStrategy getServeInstanceAllotStrategy() {
		return serveInstanceAllotStrategy;
	}

	public LocalRelayLinkAllotStrategy getRelayLinkAllotStrategy() {
		return relayLinkAllotStrategy;
	}

	public int getConnectionSize() {
		return connectionSize;
	}

	public Collection<ServeNode> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public RelayServeClusterContext setId(String id) {
		this.id = id;
		return this;
	}

	public RelayServeClusterContext setClientGuide(RelayClientGuide clientGuide) {
		this.clientGuide = clientGuide;
		return this;
	}

	public RelayServeClusterContext setConnectionSize(int connectionSize) {
		this.connectionSize = connectionSize;
		return this;
	}

	public RelayServeClusterContext setNodes(List<ServeNode> nodes) {
		this.nodes = ImmutableList.copyOf(nodes);
		return this;
	}

	public RelayServeClusterContext setRelayLinkAllotStrategy(LocalRelayLinkAllotStrategy relayLinkAllotStrategy) {
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
		return this;
	}

	public RelayServeClusterContext setServeInstanceAllotStrategy(LocalServeInstanceAllotStrategy serveInstanceAllotStrategy) {
		this.serveInstanceAllotStrategy = serveInstanceAllotStrategy;
		return this;
	}

}

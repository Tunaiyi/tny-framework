package com.tny.game.net.netty4.relay.cluster;

import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.cluster.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 12:26 下午
 */
public class SpringRelayServeClusterSetting implements RelayServeClusterSetting {

	private String id;

	private String clientGuide = defaultName(RelayClientGuide.class);

	private int connectionSize = 1;

	private String serveInstanceAllotStrategy;

	private String relayLinkAllotStrategy;

	private List<SpringRelayServeInstanceSetting> instances = new ArrayList<>();

	@Override
	public String getId() {
		return id;
	}

	public SpringRelayServeClusterSetting setId(String id) {
		this.id = id;
		return this;
	}

	public String getClientGuide() {
		return clientGuide;
	}

	public SpringRelayServeClusterSetting setClientGuide(String clientGuide) {
		this.clientGuide = clientGuide;
		return this;
	}

	@Override
	public int getConnectionSize() {
		return connectionSize;
	}

	public SpringRelayServeClusterSetting setConnectionSize(int connectionSize) {
		this.connectionSize = connectionSize;
		return this;
	}

	public boolean isHasServeInstanceAllotStrategy() {
		return StringUtils.isNoneBlank(serveInstanceAllotStrategy);
	}

	public String getServeInstanceAllotStrategy() {
		return serveInstanceAllotStrategy;
	}

	public SpringRelayServeClusterSetting setServeInstanceAllotStrategy(String serveInstanceAllotStrategy) {
		this.serveInstanceAllotStrategy = serveInstanceAllotStrategy;
		return this;
	}

	public boolean isHasRelayLinkAllotStrategy() {
		return StringUtils.isNoneBlank(relayLinkAllotStrategy);
	}

	public String getRelayLinkAllotStrategy() {
		return relayLinkAllotStrategy;
	}

	public SpringRelayServeClusterSetting setRelayLinkAllotStrategy(String relayLinkAllotStrategy) {
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
		return this;
	}

	public List<SpringRelayServeInstanceSetting> getInstances() {
		return instances;
	}

	public SpringRelayServeClusterSetting setInstances(List<SpringRelayServeInstanceSetting> instances) {
		this.instances = instances;
		return this;
	}

	@Override
	public List<ServeNode> getNodes() {
		return Collections.unmodifiableList(instances);
	}

}

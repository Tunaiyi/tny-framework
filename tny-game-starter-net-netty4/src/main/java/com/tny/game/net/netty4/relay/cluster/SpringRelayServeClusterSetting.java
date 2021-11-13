package com.tny.game.net.netty4.relay.cluster;

import com.google.common.collect.Lists;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.allot.*;
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

	private String serveName;

	private String service;

	private String username;

	private String clientGuide = defaultName(RelayClientGuide.class);

	private int linkConnectionSize = 1;

	private long linkHeartbeatInterval = 5000;

	private long linkMaxIdleTime = 10000;

	private boolean discovery = true;

	private String serveInstanceAllotStrategy = lowerCamelName(PollingRelayAllotStrategy.class);

	private String relayLinkAllotStrategy = lowerCamelName(PollingRelayAllotStrategy.class);

	private List<SpringRelayServeInstanceSetting> instances = new ArrayList<>();

	@Override
	public String getServeName() {
		return serveName;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getService() {
		return service;
	}

	public String getClientGuide() {
		return clientGuide;
	}

	@Override
	public boolean isDiscovery() {
		return discovery || StringUtils.isNoneBlank(this.serveName);
	}

	@Override
	public long getLinkHeartbeatInterval() {
		return linkHeartbeatInterval;
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
	public List<RelayServeInstanceSetting> getInstanceList() {
		return Lists.newArrayList(instances);
	}

	public List<SpringRelayServeInstanceSetting> getInstances() {
		return instances;
	}

	public SpringRelayServeClusterSetting setUsername(String username) {
		this.username = username;
		return this;
	}

	public boolean isHasServeInstanceAllotStrategy() {
		return StringUtils.isNoneBlank(serveInstanceAllotStrategy);
	}

	public String getServeInstanceAllotStrategy() {
		return serveInstanceAllotStrategy;
	}

	public boolean isHasRelayLinkAllotStrategy() {
		return StringUtils.isNoneBlank(relayLinkAllotStrategy);
	}

	public String getRelayLinkAllotStrategy() {
		return relayLinkAllotStrategy;
	}

	public SpringRelayServeClusterSetting setService(String service) {
		this.service = service;
		return this;
	}

	public SpringRelayServeClusterSetting setServeName(String serveName) {
		this.serveName = serveName;
		return this;
	}

	public SpringRelayServeClusterSetting setDiscovery(boolean discovery) {
		this.discovery = discovery;
		return this;
	}

	public SpringRelayServeClusterSetting setClientGuide(String clientGuide) {
		this.clientGuide = clientGuide;
		return this;
	}

	public SpringRelayServeClusterSetting setLinkConnectionSize(int linkConnectionSize) {
		this.linkConnectionSize = linkConnectionSize;
		return this;
	}

	public SpringRelayServeClusterSetting setServeInstanceAllotStrategy(String serveInstanceAllotStrategy) {
		this.serveInstanceAllotStrategy = serveInstanceAllotStrategy;
		return this;
	}

	public SpringRelayServeClusterSetting setRelayLinkAllotStrategy(String relayLinkAllotStrategy) {
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
		return this;
	}

	public SpringRelayServeClusterSetting setInstances(List<SpringRelayServeInstanceSetting> instances) {
		this.instances = instances;
		return this;
	}

	public SpringRelayServeClusterSetting setLinkMaxIdleTime(long linkMaxIdleTime) {
		this.linkMaxIdleTime = linkMaxIdleTime;
		return this;
	}

	public SpringRelayServeClusterSetting setLinkHeartbeatInterval(long linkHeartbeatInterval) {
		this.linkHeartbeatInterval = linkHeartbeatInterval;
		return this;
	}

}

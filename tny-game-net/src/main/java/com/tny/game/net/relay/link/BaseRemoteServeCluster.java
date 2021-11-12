package com.tny.game.net.relay.link;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.allot.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.builder.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 4:37 上午
 */
public abstract class BaseRemoteServeCluster implements NetRemoteServeCluster {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseRemoteServeCluster.class);

	private final String service;

	private final String serveName;

	private final String username;

	private final ServeInstanceAllotStrategy instanceAllotStrategy;

	private final RelayLinkAllotStrategy relayLinkAllotStrategy;

	private volatile Map<Long, NetRemoteServeInstance> instanceMap = new ConcurrentHashMap<>();

	private volatile List<NetRemoteServeInstance> instances = ImmutableList.of();

	private volatile List<NetRemoteServeInstance> healthyInstances = ImmutableList.of();

	public AtomicBoolean close = new AtomicBoolean(false);

	@Override
	public RemoteServeInstance getLocalInstance(long id) {
		return instanceMap.get(id);
	}

	public NetRemoteServeInstance instanceOf(long id) {
		return instanceMap.get(id);
	}

	protected List<NetRemoteServeInstance> instances() {
		return instances;
	}

	@Override
	public List<ServeInstance> getInstances() {
		return as(instances);
	}

	@Override
	public List<RemoteServeInstance> getHealthyLocalInstances() {
		return as(healthyInstances);
	}

	public BaseRemoteServeCluster(
			String serveName,
			String service,
			String username,
			ServeInstanceAllotStrategy instanceAllotStrategy,
			RelayLinkAllotStrategy relayLinkAllotStrategy) {
		this.serveName = serveName;
		this.username = username;
		this.service = service;
		this.instanceAllotStrategy = instanceAllotStrategy;
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
	}

	@Override
	public String getServeName() {
		return serveName;
	}

	@Override
	public String getService() {
		return service;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isClose() {
		return close.get();
	}

	@Override
	public RemoteRelayLink allotLink(Tunnel<?> tunnel) {
		RemoteServeInstance instance = instanceAllotStrategy.allot(tunnel, this);
		if (instance == null) {
			return null;
		}
		return relayLinkAllotStrategy.allot(tunnel, instance);
	}

	@Override
	public RemoteServeInstance registerInstance(NetRemoteServeInstance instance) {
		RemoteServeInstance old = instanceMap.putIfAbsent(instance.getId(), instance);
		if (old == null) {
			this.doRefreshInstances();
		}
		return instance;
	}

	@Override
	public synchronized void unregisterInstance(long instanceId) {
		NetRemoteServeInstance instance = instanceMap.remove(instanceId);
		if (instance != null && !instance.isClose()) {
			instance.close();
			this.doRefreshInstances();
		}
	}

	@Override
	public void updateInstance(ServeNode node) {
		NetRemoteServeInstance instance = instanceMap.get(node.getId());
		if (instance != null) {
			instance.updateMetadata(node.getMetadata());
			if (instance.updateHealthy(node.isHealthy())) {
				this.refreshInstances();
			}
		}
	}

	@Override
	public synchronized void refreshInstances() {
		this.doRefreshInstances();
	}

	private void doRefreshInstances() {
		this.instances = ImmutableList.sortedCopyOf(instanceMap.values());
		this.healthyInstances = ImmutableList.copyOf(instances.stream()
				.filter(NetRemoteServeInstance::isHealthy)
				.collect(Collectors.toList()));
	}

	@Override
	public synchronized void close() {
		if (close.compareAndSet(false, true)) {
			List<NetRemoteServeInstance> oldList = instances;
			instances = ImmutableList.of();
			oldList.forEach(NetRemoteServeInstance::close);
			instanceMap = new ConcurrentHashMap<>();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BaseRemoteServeCluster)) {
			return false;
		}

		BaseRemoteServeCluster that = (BaseRemoteServeCluster)o;

		return new EqualsBuilder().append(getServeName(), that.getServeName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getServeName()).toHashCode();
	}

}

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
public abstract class BaseLocalServeCluster implements NetLocalServeCluster {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseLocalServeCluster.class);

	private final String serveName;

	private final LocalServeInstanceAllotStrategy instanceAllotStrategy;

	private final LocalRelayLinkAllotStrategy relayLinkAllotStrategy;

	private volatile Map<Long, NetLocalServeInstance> instanceMap = new ConcurrentHashMap<>();

	private volatile List<NetLocalServeInstance> instances = ImmutableList.of();

	private volatile List<NetLocalServeInstance> healthyInstances = ImmutableList.of();

	public AtomicBoolean close = new AtomicBoolean(false);

	@Override
	public LocalServeInstance getLocalInstance(long id) {
		return instanceMap.get(id);
	}

	public NetLocalServeInstance instanceOf(long id) {
		return instanceMap.get(id);
	}

	protected List<NetLocalServeInstance> instances() {
		return instances;
	}

	@Override
	public List<ServeInstance> getInstances() {
		return as(instances);
	}

	@Override
	public List<LocalServeInstance> getHealthyLocalInstances() {
		return as(healthyInstances);
	}

	public BaseLocalServeCluster(String serveName,
			LocalServeInstanceAllotStrategy instanceAllotStrategy,
			LocalRelayLinkAllotStrategy relayLinkAllotStrategy) {
		this.serveName = serveName;
		this.instanceAllotStrategy = instanceAllotStrategy;
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
	}

	@Override
	public String getServeName() {
		return serveName;
	}

	@Override
	public boolean isClose() {
		return close.get();
	}

	@Override
	public LocalRelayLink allotLink(Tunnel<?> tunnel) {
		LocalServeInstance instance = instanceAllotStrategy.allot(tunnel, this);
		if (instance == null) {
			return null;
		}
		return relayLinkAllotStrategy.allot(tunnel, instance);
	}

	@Override
	public LocalServeInstance registerInstance(NetLocalServeInstance instance) {
		LocalServeInstance old = instanceMap.putIfAbsent(instance.getId(), instance);
		if (old == null) {
			this.doRefreshInstances();
		}
		return instance;
	}

	@Override
	public synchronized void unregisterInstance(long instanceId) {
		NetLocalServeInstance instance = instanceMap.remove(instanceId);
		if (instance != null && !instance.isClose()) {
			instance.close();
			this.doRefreshInstances();
		}
	}

	@Override
	public void updateInstance(ServeNode node) {
		NetLocalServeInstance instance = instanceMap.get(node.getId());
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
				.filter(NetLocalServeInstance::isHealthy)
				.collect(Collectors.toList()));
	}

	@Override
	public synchronized void close() {
		if (close.compareAndSet(false, true)) {
			List<NetLocalServeInstance> oldList = instances;
			instances = ImmutableList.of();
			oldList.forEach(NetLocalServeInstance::close);
			instanceMap = new ConcurrentHashMap<>();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BaseLocalServeCluster)) {
			return false;
		}

		BaseLocalServeCluster that = (BaseLocalServeCluster)o;

		return new EqualsBuilder().append(getServeName(), that.getServeName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getServeName()).toHashCode();
	}

}

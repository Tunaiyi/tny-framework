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

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 4:37 上午
 */
public abstract class BaseLocalServeCluster implements LocalServeCluster {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseLocalServeCluster.class);

	private final String id;

	private final LocalServeInstanceAllotStrategy instanceAllotStrategy;

	private final LocalRelayLinkAllotStrategy relayLinkAllotStrategy;

	private volatile Map<Long, LocalServeInstance> instanceMap = new ConcurrentHashMap<>();

	private volatile List<LocalServeInstance> instances = ImmutableList.of();

	public AtomicBoolean close = new AtomicBoolean(false);

	@Override
	public List<ServeInstance> getInstances() {
		return as(instances);
	}

	@Override
	public List<LocalServeInstance> getLocalInstances() {
		return as(instances);
	}

	public BaseLocalServeCluster(String id,
			LocalServeInstanceAllotStrategy instanceAllotStrategy,
			LocalRelayLinkAllotStrategy relayLinkAllotStrategy) {
		this.id = id;
		this.instanceAllotStrategy = instanceAllotStrategy;
		this.relayLinkAllotStrategy = relayLinkAllotStrategy;
	}

	@Override
	public String getId() {
		return id;
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
	public LocalServeInstance registerInstance(LocalServeInstance instance) {
		LocalServeInstance old = instanceMap.putIfAbsent(instance.getId(), instance);
		if (old == null) {
			this.instances = ImmutableList.sortedCopyOf(instanceMap.values());
		}
		return instance;
	}

	@Override
	public synchronized void unregisterInstance(long instanceId) {
		LocalServeInstance instance = instanceMap.remove(instanceId);
		if (instance != null && !instance.isClose()) {
			instance.close();
			this.instances = ImmutableList.sortedCopyOf(instanceMap.values());
		}
	}

	//	@Override
	//	public synchronized T registerLink(NetRelayLink link) {
	//		T instance = instanceMap.get(link.getInstanceId());
	//		if (instance == null) {
	//			LOGGER.warn("注册 {} 时候, 未找到 {}-{} 服务器节点实例对象, 关闭连接.", link, link.getClusterId(), link.getInstanceId());
	//			link.close();
	//			return null;
	//		}
	//		//		instance.register(link);
	//		return instance;
	//	}
	//
	//	@Override
	//	public void relieveLink(NetRelayLink link) {
	//		LocalServeInstance instance = instanceMap.get(link.getInstanceId());
	//		if (instance != null) {
	//			//			instance.relieve(link);
	//		}
	//	}

	@Override
	public synchronized void close() {
		if (close.compareAndSet(false, true)) {
			List<LocalServeInstance> oldList = instances;
			instances = ImmutableList.of();
			oldList.forEach(LocalServeInstance::close);
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

		return new EqualsBuilder().append(getId(), that.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
	}

}

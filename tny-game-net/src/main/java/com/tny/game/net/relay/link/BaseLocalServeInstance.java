package com.tny.game.net.relay.link;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.relay.cluster.*;
import org.apache.commons.lang3.builder.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 4:39 上午
 */
public class BaseLocalServeInstance implements LocalServeInstance {

	protected final LocalServeCluster cluster;

	private final long id;

	private final String scheme;

	private final String host;

	private final int port;

	private final AtomicBoolean close = new AtomicBoolean(false);

	private Map<String, LocalRelayLink> relayLinkMap = new ConcurrentHashMap<>();

	private volatile List<LocalRelayLink> relayLinks = ImmutableList.of();

	public BaseLocalServeInstance(LocalServeCluster cluster, ServeNode node) {
		this.id = node.getId();
		this.scheme = node.getScheme();
		this.host = node.getHost();
		this.port = node.getPort();
		this.cluster = cluster;
	}

	@Override
	public String getClusterId() {
		return cluster.getId();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public List<LocalRelayLink> getRelayLinks() {
		return relayLinks;
	}

	@Override
	public boolean isClose() {
		return close.get();
	}

	@Override
	public synchronized void close() {
		if (close.compareAndSet(false, true)) {
			this.prepareClose();
			Map<String, LocalRelayLink> oldMap = this.relayLinkMap;
			this.relayLinkMap = new ConcurrentHashMap<>();
			oldMap.forEach((id, link) -> link.close());
			oldMap.clear();
			this.prepareClose();
		}
	}

	protected void prepareClose() {
	}

	protected void postClose() {
	}

	@Override
	public synchronized void register(RelayLink link) {
		if (link instanceof LocalRelayLink) {
			NetRelayLink old = relayLinkMap.put(link.getId(), as(link));
			if (old != null && old != link) {
				old.close();
			}
			relayLinks = ImmutableList.sortedCopyOf(Comparator.comparing(LocalRelayLink::getId), relayLinkMap.values());
		}
	}

	@Override
	public synchronized void relieve(RelayLink link) {
		if (link instanceof LocalRelayLink) {
			if (relayLinkMap.remove(link.getId(), link)) {
				if (link.isActive()) {
					link.close();
				}
				relayLinks = ImmutableList.sortedCopyOf(Comparator.comparing(LocalRelayLink::getId), relayLinkMap.values());
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BaseLocalServeInstance)) {
			return false;
		}

		BaseLocalServeInstance that = (BaseLocalServeInstance)o;

		return new EqualsBuilder().append(getId(), that.getId()).append(cluster, that.cluster).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(cluster).append(getId()).toHashCode();
	}

}

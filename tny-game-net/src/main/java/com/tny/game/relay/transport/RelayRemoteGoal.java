package com.tny.game.relay.transport;

import com.tny.game.common.utils.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 4:37 上午
 */
public class RelayRemoteGoal implements NetRelayLinkAllocator {

	private final String serverType;

	private final AtomicBoolean close = new AtomicBoolean(false);

	private volatile Map<Integer, RelayLinkNode> linkNodeMap = new ConcurrentHashMap<>();

	private final NavigableSet<Integer> linkNodes = new ConcurrentSkipListSet<>();

	public String getServerType() {
		return serverType;
	}

	public RelayRemoteGoal(String serverType) {
		this.serverType = serverType;
	}

	@Override
	public NetRelayLink allot(NetTunnel<?> tunnel) {
		Object value = tunnel.getUserId();
		int hash = HashAide.bkdrStringHash32(String.valueOf(value)) % 65535;
		Integer id = linkNodes.higher(hash);
		if (id == null) {
			id = linkNodes.first();
		}
		if (id != null) {
			RelayLinkNode node = linkNodeMap.get(id);
			if (node == null) {
				return null;
			}
			return node.allot(tunnel);
		}
		return null;
	}

	public synchronized void close() {
		if (close.compareAndSet(false, true)) {
			Map<Integer, RelayLinkNode> oldMap = this.linkNodeMap;
			this.linkNodeMap = new ConcurrentHashMap<>();
			oldMap.forEach((id, node) -> node.close());
			oldMap.clear();
			this.linkNodes.clear();
		}
	}

	@Override
	public boolean isClose() {
		return close.get();
	}

	@Override
	public boolean isActive() {
		return close.get() && !linkNodeMap.isEmpty();
	}

}

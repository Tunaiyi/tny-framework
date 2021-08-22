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
 * @date : 2021/8/21 4:39 上午
 */
public class RelayLinkNode {

	private final AtomicBoolean close = new AtomicBoolean(false);

	private Map<Integer, NetRelayLink> relayLinkMap = new ConcurrentHashMap<>();

	private final NavigableSet<Integer> relayLinks = new ConcurrentSkipListSet<>();

	public NetRelayLink allot(Tunnel<?> tunnel) {
		Object value = tunnel.getUserId();
		int hash = HashAide.bkdrStringHash32(String.valueOf(value)) % 65535;
		Integer id = relayLinks.higher(hash);
		if (id == null) {
			id = relayLinks.first();
		}
		if (id != null) {
			return relayLinkMap.get(id);
		}
		return null;
	}

	public void close() {
		if (close.compareAndSet(false, true)) {
			Map<Integer, NetRelayLink> oldMap = this.relayLinkMap;
			this.relayLinkMap = new ConcurrentHashMap<>();
			oldMap.forEach((id, node) -> node.close());
			oldMap.clear();
			this.relayLinks.clear();
		}
	}

}

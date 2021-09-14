package com.tny.game.net.relay.cluster;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/13 12:10 下午
 */
public class ServeNodeChange {

	private final ServeNode node;

	private final List<ServeNodeChangeStatus> changeStatuses;

	public ServeNodeChange(ServeNode node, List<ServeNodeChangeStatus> changeStatuses) {
		this.node = node;
		this.changeStatuses = changeStatuses;
	}

	public ServeNode getNode() {
		return node;
	}

	public List<ServeNodeChangeStatus> getChangeStatuses() {
		return changeStatuses;
	}

}

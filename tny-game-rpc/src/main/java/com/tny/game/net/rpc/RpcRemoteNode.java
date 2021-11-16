package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.endpoint.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 3:31 下午
 */
public class RpcRemoteNode {

	private final long serverId;

	private final Map<Long, Endpoint<RpcLinkerId>> realEndpointMap = new ConcurrentHashMap<>();

	private volatile List<Endpoint<RpcLinkerId>> orderEndpoints = ImmutableList.of();

	;

	private final RpcRemoteServicer service;

	public RpcRemoteNode(long serverId, RpcRemoteServicer service) {
		this.serverId = serverId;
		this.service = service;
	}

	public long getServerId() {
		return serverId;
	}

	public List<Endpoint<RpcLinkerId>> getOrderEndpoints() {
		return orderEndpoints;
	}

	public boolean isActive() {
		return !orderEndpoints.isEmpty();
	}

	protected void addEndpoint(Endpoint<RpcLinkerId> endpoint) {
		synchronized (this) {
			boolean activate = this.realEndpointMap.isEmpty();
			RpcLinkerId nodeId = endpoint.getUserId();
			this.realEndpointMap.put(nodeId.getId(), endpoint);
			this.orderEndpoints = ImmutableList.sortedCopyOf(
					Comparator.comparing(Endpoint::getUserId),
					realEndpointMap.values());
			if (!activate && !this.realEndpointMap.isEmpty()) {
				service.onNodeActivate(this);
			}
		}
	}

	protected void removeEndpoint(Endpoint<RpcLinkerId> endpoint) {
		synchronized (this) {
			RpcLinkerId nodeId = endpoint.getUserId();
			boolean activate = this.realEndpointMap.isEmpty();
			if (this.realEndpointMap.remove(nodeId.getId(), endpoint)) {
				this.orderEndpoints = ImmutableList.sortedCopyOf(
						Comparator.comparing(Endpoint::getUserId),
						realEndpointMap.values());
				if (activate && this.realEndpointMap.isEmpty()) {
					service.onNodeUnactivated(this);
				}
			}
		}
	}

}
package com.tny.game.net.relay.link;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.route.*;
import com.tny.game.net.transport.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 8:00 下午
 */
public class GeneralLocalRelayTunnel<UID> extends BaseServerTunnel<UID, NetSession<UID>, MessageTransporter<UID>> implements LocalRelayTunnel<UID> {

	/**
	 * 当前服务器的服务实例 id
	 */
	private final long instanceId;

	/**
	 * 关联的 link
	 */
	private Map<String, LocalRelayLink> linkMap = new CopyOnWriteMap<>();

	/**
	 * 关联的 link
	 */
	private Map<String, LocalTunnelRelayer> relayerMap;

	/**
	 * 消息路由器
	 */
	private final RelayMessageRouter relayMessageRouter;

	public GeneralLocalRelayTunnel(long instanceId, long id, MessageTransporter<UID> transport,
			NetworkContext context, RelayMessageRouter relayMessageRouter) {
		super(id, transport, context);
		this.instanceId = instanceId;
		this.relayMessageRouter = relayMessageRouter;
	}

	public GeneralLocalRelayTunnel<UID> initRelayers(Map<String, LocalTunnelRelayer> relayerMap) {
		this.relayerMap = ImmutableMap.copyOf(relayerMap);
		return this;
	}

	@Override
	protected void onClose() {
		super.onClose();
		Map<String, LocalRelayLink> linkMap = this.linkMap;
		this.linkMap = new CopyOnWriteMap<>();
		for (LocalRelayLink link : linkMap.values()) {
			link.closeTunnel(this);
		}
	}

	@Override
	public WriteMessageFuture relay(Message message, boolean needPromise) {
		WriteMessagePromise promise = needPromise ? this.createWritePromise() : null;
		String id = relayMessageRouter.route(this, message);
		if (id == null) {
			id = "-None";
		}
		LocalTunnelRelayer relayer = relayerMap.get(id);
		if (relayer != null) {
			return relayer.relay(this, message, promise);
		} else {
			ResultCode code = NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR;
			LOGGER.warn("# Tunnel ({}) 服务器主动关闭连接, 不支持 {} 集群", this, id);
			TunnelAides.responseMessage(this, MessageContexts.push(Protocols.PUSH, code));
			if (promise != null) {
				promise.failed(new NetGeneralException(code));
			}
		}
		return promise;
	}

	@Override
	public long getInstanceId() {
		return instanceId;
	}

	@Override
	public void bindLink(LocalRelayLink link) {
		synchronized (this) {
			LocalRelayLink old = linkMap.put(link.getServeName(), link);
			if (old != null) {
				old.delinkTunnel(this);
				link.switchTunnel(this);
			} else {
				link.openTunnel(this);
			}
		}
	}

	@Override
	public void unbindLink(LocalRelayLink link) {
		synchronized (this) {
			if (linkMap.remove(link.getServeName(), link)) {
				link.closeTunnel(this);
			}
		}
	}

	@Override
	public LocalRelayLink getLink(String serveName) {
		return linkMap.get(serveName);
	}

	@Override
	public Set<String> getLinkKeys() {
		return linkMap.keySet();
	}

}

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
public class GeneralRemoteRelayTunnel<UID> extends BaseServerTunnel<UID, NetSession<UID>, MessageTransporter<UID>> implements RemoteRelayTunnel<UID> {

	/**
	 * 当前服务器的服务实例 id
	 */
	private final long instanceId;

	/**
	 * 关联的 link
	 */
	private Map<String, RemoteRelayLink> linkMap = new CopyOnWriteMap<>();

	/**
	 * 关联的 link
	 */
	private Map<String, LocalTunnelRelayer> relayerMap;

	/**
	 * 消息路由器
	 */
	private final RelayMessageRouter relayMessageRouter;

	public GeneralRemoteRelayTunnel(long instanceId, long id, MessageTransporter<UID> transport, NetworkContext context,
			RelayMessageRouter relayMessageRouter) {
		super(id, transport, context);
		this.instanceId = instanceId;
		this.relayMessageRouter = relayMessageRouter;
	}

	public GeneralRemoteRelayTunnel<UID> initRelayers(Map<String, LocalTunnelRelayer> relayerMap) {
		this.relayerMap = ImmutableMap.copyOf(relayerMap);
		return this;
	}

	@Override
	protected void onClose() {
		super.onClose();
		Map<String, RemoteRelayLink> linkMap = this.linkMap;
		this.linkMap = new CopyOnWriteMap<>();
		for (RemoteRelayLink link : linkMap.values()) {
			link.closeTunnel(this);
		}
	}

	@Override
	public MessageWriteAwaiter relay(Message message, boolean needPromise) {
		MessageWriteAwaiter promise = needPromise ? new MessageWriteAwaiter() : null;
		String service = relayMessageRouter.route(this, message);
		if (service == null) {
			service = "-None";
		}
		LocalTunnelRelayer relayer = relayerMap.get(service);
		if (relayer != null) {
			return relayer.relay(this, message, promise);
		} else {
			ResultCode code = NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR;
			LOGGER.warn("# Tunnel ({}) 服务器主动关闭连接, 不支持 {} 集群", this, service);
			TunnelAide.responseMessage(this, MessageContexts.push(Protocols.PUSH, code));
			if (promise != null) {
				promise.completeExceptionally(new NetGeneralException(code));
			}
		}
		return promise;
	}

	@Override
	public long getInstanceId() {
		return instanceId;
	}

	@Override
	public void bindLink(RemoteRelayLink link) {
		synchronized (this) {
			RemoteRelayLink old = linkMap.put(link.getService(), link);
			if (old != null) {
				old.delinkTunnel(this);
				link.switchTunnel(this);
			} else {
				link.openTunnel(this);
			}
		}
	}

	@Override
	public void unbindLink(RemoteRelayLink link) {
		synchronized (this) {
			if (linkMap.remove(link.getService(), link)) {
				link.closeTunnel(this);
			}
		}
	}

	@Override
	public RemoteRelayLink getLink(String service) {
		return linkMap.get(service);
	}

	@Override
	public Set<String> getLinkKeys() {
		return linkMap.keySet();
	}

}

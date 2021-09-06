package com.tny.game.net.relay.link;

import com.tny.game.common.concurrent.collection.*;
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

	private final long instanceId;

	private Map<String, LocalRelayLink> linkMap = new CopyOnWriteMap<>();

	private final RelayMessageRouter relayMessageRouter;

	private final LocalRelayExplorer localRelayExplorer;

	public GeneralLocalRelayTunnel(long instanceId, long id,
			MessageTransporter<UID> transport, NetworkContext context,
			LocalRelayExplorer localRelayExplorer, RelayMessageRouter relayMessageRouter) {
		super(id, transport, context);
		this.instanceId = instanceId;
		this.relayMessageRouter = relayMessageRouter;
		this.localRelayExplorer = localRelayExplorer;
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
			id = "";
		}
		LocalRelayLink link = this.getLink(id);
		if (link == null || !link.isActive()) {
			link = localRelayExplorer.allotLink(this, id);
		}
		if (link != null && link.isActive()) {
			return link.relay(this, message, promise);
		} else {
			if (promise != null) {
				promise.failed(new NetGeneralException(NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR));
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
			LocalRelayLink old = linkMap.put(link.getClusterId(), link);
			if (old != null) {
				link.switchTunnel(this);
			} else {
				link.openTunnel(this);
			}
		}
	}

	@Override
	public void unbindLink(LocalRelayLink link) {
		synchronized (this) {
			if (linkMap.remove(link.getClusterId(), link)) {
				link.closeTunnel(this);
			}
		}
	}

	@Override
	public LocalRelayLink getLink(String clusterId) {
		return linkMap.get(clusterId);
	}

	@Override
	public Set<String> getLinkKeys() {
		return linkMap.keySet();
	}

}

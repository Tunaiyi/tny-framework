package com.tny.game.net.relay.link;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.route.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 8:00 下午
 */
public class GeneralLocalRelayTunnel<UID> implements LocalRelayTunnel<UID> {

	private final Map<String, TunnelRelayLinker> linkMap = new CopyOnWriteMap<>();

	private final NetTunnel<UID> tunnel;

	private final RelayMessageRouter relayMessageRouter;

	public GeneralLocalRelayTunnel(NetTunnel<UID> tunnel, Map<String, TunnelRelayLinker> linkMap, RelayMessageRouter relayMessageRouter) {
		this.tunnel = tunnel;
		this.linkMap.putAll(linkMap);
		if (linkMap.size() == 1 || relayMessageRouter == null) {
			this.relayMessageRouter = new FirstRelayMessageRouter();
		} else {
			this.relayMessageRouter = relayMessageRouter;
		}
	}

	@Override
	public long getId() {
		return tunnel.getId();
	}

	@Override
	public long getAccessId() {
		return tunnel.getAccessId();
	}

	@Override
	public TunnelMode getMode() {
		return tunnel.getMode();
	}

	@Override
	public boolean isActive() {
		return tunnel.isActive();
	}

	@Override
	public boolean isOpen() {
		return tunnel.isOpen();
	}

	@Override
	public TunnelStatus getStatus() {
		return tunnel.getStatus();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return tunnel.getRemoteAddress();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return tunnel.getLocalAddress();
	}

	@Override
	public NetEndpoint<UID> getEndpoint() {
		return tunnel.getEndpoint();
	}

	@Override
	public UID getUserId() {
		return tunnel.getUserId();
	}

	@Override
	public String getUserType() {
		return tunnel.getUserType();
	}

	@Override
	public Certificate<UID> getCertificate() {
		return tunnel.getCertificate();
	}

	@Override
	public boolean isLogin() {
		return tunnel.isLogin();
	}

	@Override
	public boolean isClosed() {
		return tunnel.isClosed();
	}

	@Override
	public boolean close() {
		if (tunnel.close()) {
			linkMap.forEach((id, linker) -> linker.link().destroyTunnel(this));
			return true;
		}
		return false;
	}

	@Override
	public boolean open() {
		boolean success = true;
		for (TunnelRelayLinker linker : linkMap.values()) {
			success = success && linker.link().bindTunnel(this);
		}
		if (success) {
			this.tunnel.open();
			return true;
		} else {
			this.close();
			return false;
		}
	}

	@Override
	public Attributes attributes() {
		return tunnel.attributes();
	}

	@Override
	public void setAccessId(long accessId) {
		tunnel.setAccessId(accessId);
	}

	@Override
	public void ping() {
		tunnel.ping();
	}

	@Override
	public void pong() {
		tunnel.pong();
	}

	@Override
	public void disconnect() {
		tunnel.disconnect();
	}

	@Override
	public void reset() {
		tunnel.reset();
	}

	@Override
	public boolean bind(NetEndpoint<UID> endpoint) {
		return tunnel.bind(endpoint);
	}

	@Override
	public NetworkContext<UID> getContext() {
		return tunnel.getContext();
	}

	@Override
	public boolean receive(Message message) {
		return tunnel.receive(message);
	}

	@Override
	public SendContext send(MessageContext messageContext) {
		return tunnel.send(messageContext);
	}

	@Override
	public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
		return tunnel.write(message, promise);
	}

	@Override
	public WriteMessageFuture write(MessageAllocator allocator, MessageContext context) throws NetException {
		return tunnel.write(allocator, context);
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return tunnel.createWritePromise();
	}

	@Override
	public WriteMessageFuture relay(Message message, boolean needPromise) {
		WriteMessagePromise promise = needPromise ? this.createWritePromise() : null;
		LocalRelayLink link = relayMessageRouter.route(this, message, this.linkMap);
		if (link != null && link.isActive()) {
			return link.relay(this.tunnel.getId(), message, promise);
		} else {
			if (promise != null) {
				promise.failed(new NetGeneralException(NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR));
			}
		}
		return promise;
	}

	@Override
	public void closeOnLink(NetRelayLink closeOne) {
		String id = closeOne.getId();
		TunnelRelayLinker localLink = linkMap.get(id);
		LocalRelayLink link = localLink.link();
		if (link == closeOne && localLink.getImportance() == ServeClusterImportance.REQUIRED) {
			this.close();
		}
	}

}

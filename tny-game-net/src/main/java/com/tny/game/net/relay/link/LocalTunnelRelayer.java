package com.tny.game.net.relay.link;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * 本地通讯通道与目标服务中继器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/7 1:06 下午
 */
public class LocalTunnelRelayer {

	public static final Logger LOGGER = LoggerFactory.getLogger(LocalTunnelRelayer.class);

	private final String serveName;

	private final ServeClusterFilterStatus filterStatus;

	private final RemoteRelayExplorer remoteRelayExplorer;

	public LocalTunnelRelayer(String serveName, ServeClusterFilterStatus filterStatus,
			RemoteRelayExplorer remoteRelayExplorer) {
		this.serveName = serveName;
		this.filterStatus = filterStatus;
		this.remoteRelayExplorer = remoteRelayExplorer;
	}

	public String getServeName() {
		return serveName;
	}

	public WriteMessageFuture relay(RemoteRelayTunnel<?> tunnel, Message message, WriteMessagePromise promise) {
		RemoteRelayLink link = allot(tunnel);
		if (link != null && link.isActive()) {
			return link.relay(tunnel, message, promise);
		} else {
			ResultCode code = NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR;
			if (promise != null) {
				promise.failed(new NetGeneralException(code));
			}
			LOGGER.warn("# Tunnel ({}) 服务器主动关闭连接, {} 集群无可以用 link", tunnel, this.serveName);
			TunnelAides.responseMessage(tunnel, MessageContexts.push(Protocols.PUSH, code));
		}
		return promise;
	}

	public RemoteRelayLink allot(RemoteRelayTunnel<?> tunnel) {
		RemoteRelayLink link = tunnel.getLink(serveName);
		if (link != null && link.isActive()) {
			return link;
		}
		for (int i = 0; i < 3; i++) {
			link = remoteRelayExplorer.allotLink(tunnel, serveName);
			if (link == null) {
				return null;
			}
			if (!link.isActive()) {
				continue;
			}
			tunnel.bindLink(link);
			return link;
		}
		return null;
	}

	public ServeClusterFilterStatus getFilterStatus() {
		return filterStatus;
	}

}

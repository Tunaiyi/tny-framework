/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.relay.link;

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * 本地通讯通道与目标服务中继器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/7 1:06 下午
 */
public class ClientTunnelRelayer {

    public static final Logger LOGGER = LoggerFactory.getLogger(ClientTunnelRelayer.class);

    private final String service;

    private final ServeClusterFilterStatus filterStatus;

    private final ClientRelayExplorer remoteRelayExplorer;

    public ClientTunnelRelayer(String service, ServeClusterFilterStatus filterStatus,
            ClientRelayExplorer remoteRelayExplorer) {
        this.service = service;
        this.filterStatus = filterStatus;
        this.remoteRelayExplorer = remoteRelayExplorer;
    }

    public String getService() {
        return service;
    }

    public MessageWriteFuture relay(ClientRelayTunnel tunnel, RpcTransferContext rpcContext, MessageWriteFuture promise) {
        ClientRelayLink link = allot(tunnel);
        var message = rpcContext.getMessage();
        if (link != null && link.isActive()) {
            rpcContext.transfer(link, RpcTransactionContext.relayOperation(message));
            var future = link.relay(tunnel, message, promise);
            rpcContext.completeSilently();
            return future;
        } else {
            ResultCode code = NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR;
            if (promise != null) {
                promise.completeExceptionally(new RelayException(code));
            }
            LOGGER.warn("# Tunnel ({}) 服务器主动关闭连接, {} 集群无可以用 link", tunnel, this.service);
            rpcContext.complete(code);
        }
        return promise;
    }

    public ClientRelayLink allot(ClientRelayTunnel tunnel) {
        ClientRelayLink link = tunnel.getLink(service);
        if (link != null && link.isActive()) {
            return link;
        }
        for (int i = 0; i < 3; i++) {
            link = remoteRelayExplorer.allotLink(tunnel, service);
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

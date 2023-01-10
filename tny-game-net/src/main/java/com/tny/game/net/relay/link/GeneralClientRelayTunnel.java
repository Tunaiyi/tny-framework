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

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.relay.link.route.*;
import com.tny.game.net.transport.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 8:00 下午
 */
public class GeneralClientRelayTunnel<UID> extends ServerTunnel<UID, NetSession<UID>, MessageTransporter> implements ClientRelayTunnel<UID> {

    /**
     * 当前服务器的服务实例 id
     */
    private final long instanceId;

    /**
     * 关联的 link
     */
    private Map<String, ClientRelayLink> linkMap = new CopyOnWriteMap<>();

    /**
     * 关联的 link
     */
    private Map<String, ClientTunnelRelayer> relayerMap;

    /**
     * 消息路由器
     */
    private final RelayMessageRouter relayMessageRouter;

    public GeneralClientRelayTunnel(long instanceId, long id, MessageTransporter transport, NetworkContext context,
            RelayMessageRouter relayMessageRouter) {
        super(id, transport, context);
        this.instanceId = instanceId;
        this.relayMessageRouter = relayMessageRouter;
    }

    public GeneralClientRelayTunnel<UID> initRelayers(Map<String, ClientTunnelRelayer> relayerMap) {
        this.relayerMap = ImmutableMap.copyOf(relayerMap);
        return this;
    }

    @Override
    protected void onClose() {
        super.onClose();
        Map<String, ClientRelayLink> linkMap = this.linkMap;
        this.linkMap = new CopyOnWriteMap<>();
        for (ClientRelayLink link : linkMap.values()) {
            link.closeTunnel(this);
        }
    }

    @Override
    public MessageWriteFuture relay(RpcProviderContext rpcContext, boolean needPromise) {
        MessageWriteFuture promise = needPromise ? new MessageWriteFuture() : null;
        var message = rpcContext.netMessage();
        String service = relayMessageRouter.route(this, message);
        if (service == null) {
            service = "-None";
        }
        ClientTunnelRelayer relayer = relayerMap.get(service);
        if (relayer != null) {
            return relayer.relay(this, rpcContext, promise);
        } else {
            ResultCode code = NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR;
            LOGGER.warn("# Tunnel ({}) 服务器主动关闭连接, 不支持 {} 集群", this, service);
            if (promise != null) {
                promise.completeExceptionally(new RelayException(code));
            }
            rpcContext.complete(code);
        }
        return promise;
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public void bindLink(ClientRelayLink link) {
        synchronized (this) {
            ClientRelayLink old = linkMap.put(link.getService(), link);
            if (old != null) {
                old.delinkTunnel(this);
                link.switchTunnel(this);
            } else {
                link.openTunnel(this);
            }
        }
    }

    @Override
    public void unbindLink(ClientRelayLink link) {
        synchronized (this) {
            if (linkMap.remove(link.getService(), link)) {
                link.closeTunnel(this);
            }
        }
    }

    @Override
    public ClientRelayLink getLink(String service) {
        return linkMap.get(service);
    }

    @Override
    public Set<String> getLinkKeys() {
        return linkMap.keySet();
    }

}

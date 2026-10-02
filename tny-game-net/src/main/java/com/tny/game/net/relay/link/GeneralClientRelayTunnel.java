/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.relay.link;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.relay.link.route.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 8:00 下午
 */
public class GeneralClientRelayTunnel extends ServerTransportTunnel<NetSession, MessageTransport> implements ClientRelayTunnel {

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
     * 关联的 link
     */
    private final Lock linkLock = new ReentrantLock();

    /**
     * 消息路由器
     */
    private final RelayMessageRouter relayMessageRouter;


    public GeneralClientRelayTunnel(long instanceId, long id, MessageTransport transport, NetworkContext context,
            RelayMessageRouter relayMessageRouter) {
        super(id, transport, context);
        this.instanceId = instanceId;
        this.relayMessageRouter = relayMessageRouter;
    }

    public GeneralClientRelayTunnel initRelayers(Map<String, ClientTunnelRelayer> relayerMap) {
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
    public MessageWriteFuture relay(RpcTransferContext rpcContext, boolean needPromise) {
        MessageWriteFuture promise = needPromise ? new MessageWriteFuture() : null;
        var message = rpcContext.getMessage();
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
        linkLock.lock();
        try {
            ClientRelayLink old = linkMap.put(link.getService(), link);
            if (old != null) {
                old.unlinkTunnel(this);
                link.switchTunnel(this);
            } else {
                link.openTunnel(this);
            }
        } finally {
            linkLock.unlock();
        }
    }

    @Override
    public void unbindLink(ClientRelayLink link) {
        linkLock.lock();
        try {
            if (linkMap.remove(link.getService(), link)) {
                link.closeTunnel(this);
            }
        } finally {
            linkLock.unlock();
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

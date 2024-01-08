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
package com.tny.game.net.rpc;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.rpc.auth.*;
import com.tny.game.net.rpc.setting.*;
import com.tny.game.net.session.TunnelConnector;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/10 2:22 下午
 */
public class RpcConnectorFactory implements ServedService {

    private static final ScheduledExecutorService EXECUTOR_SERVICE =
            Executors.newScheduledThreadPool(1, new CoreThreadFactory("RpcConnector"));

    private ClientGuide clientGuide;

    private NetAppContext appContext;

    private RpcClusterSetting setting;

    public RpcConnectorFactory() {
    }

    public RpcConnectorFactory(NetAppContext appContext, RpcClusterSetting setting, ClientGuide clientGuide) {
        this.clientGuide = clientGuide;
        this.appContext = appContext;
        this.setting = setting;
    }

    public RpcServiceSetting getSetting() {
        return setting;
    }

    @Override
    public String getServeName() {
        return setting.discoverService();
    }

    @Override
    public String getService() {
        return setting.serviceName();
    }

    public boolean isDiscovery() {
        return setting.isDiscovery();
    }

    private PostConnect postConnect(int index) {
        return (tunnel) -> {
            var future = new CompleteStageFuture<Boolean>();
            RpcExitContext exitContext = null;
            try {
                String username = StringAide.ifBlank(setting.getUsername(), appContext.getService());
                RpcServiceType serviceType = RpcServiceTypes.checkService(username);
                int serverId = appContext.getServerId();
                long id = RpcAccessIdentify.formatId(serviceType, serverId, index);
                RequestContent content = RpcAuthMessageContexts
                        .authRequest(id, setting.getPassword())
                        .willRespondFuture(setting.getAuthenticateTimeout());
                RpcExitContext invokeContext = RpcTransactionContext.createExit(tunnel.getSession(), content, true,
                        tunnel.getContext().getRpcMonitor());
                exitContext = invokeContext;
                invokeContext.invoke(RpcTransactionContext.rpcOperation(RpcAuthController.class, "authenticate", content));
                MessageSent receipt = tunnel.send(content);
                receipt.respond().whenComplete((message, cause) -> {
                    if (cause != null) {
                        invokeContext.complete(cause);
                        future.completeExceptionally(cause);
                    } else {
                        invokeContext.complete(message);
                        future.complete(true);
                    }
                });
            } catch (Throwable error) {
                if (exitContext != null) {
                    exitContext.complete(error);
                }
                throw error;
            }
            return future;
        };
    }

    public TunnelConnector create(int index, URL url) {
        return new CommonTunnelConnector(clientGuide, url, postConnect(index), EXECUTOR_SERVICE);
    }

    public RpcConnectorFactory setClientGuide(ClientGuide clientGuide) {
        this.clientGuide = clientGuide;
        return this;
    }

    public RpcConnectorFactory setAppContext(NetAppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    public RpcConnectorFactory setSetting(RpcClusterSetting setting) {
        this.setting = setting;
        return this;
    }
}

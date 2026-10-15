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

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.rpc;

import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.rpc.auth.*;
import com.tny.game.net.rpc.setting.*;
import com.tny.game.net.serve.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/10 2:22 下午
 */
public class RpcClientFactory implements Serve {

    private ClientGuide clientGuide;

    private NetAppContext appContext;

    private RpcServiceSetting setting;

    public RpcClientFactory() {
    }

    public RpcClientFactory(NetAppContext appContext, RpcServiceSetting setting, ClientGuide clientGuide) {
        this.clientGuide = clientGuide;
        this.appContext = appContext;
        this.setting = setting;
    }

    public RpcServiceSetting getSetting() {
        return setting;
    }

    @Override
    public String getServeName() {
        return setting.getServeName();
    }

    @Override
    public String getService() {
        return setting.getService();
    }

    public boolean isDiscovery() {
        return setting.isDiscovery();
    }

    private <ID> PostConnect<ID> postConnect(int index) {
        return (c) -> {
            String user = StringAide.ifBlank(setting.getUsername(), appContext.getAppType());
            RpcServiceType serviceType = RpcServiceTypes.checkService(user);
            int serverId = appContext.getServerId();
            long id = RpcAccessIdentify.formatId(serviceType, serverId, index);
            RequestContext context = RpcAuthMessageContexts
                    .authRequest(id, setting.getPassword())
                    .willRespondAwaiter(3000L);
            c.send(context);
            context.respond().get(12000, TimeUnit.MILLISECONDS);
            return true;
        };
    }

    public <UID> Client<UID> create(int index, URL url) {
        return clientGuide.client(url, postConnect(index));
    }

    public RpcClientFactory setClientGuide(ClientGuide clientGuide) {
        this.clientGuide = clientGuide;
        return this;
    }

    public RpcClientFactory setAppContext(NetAppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    public RpcClientFactory setSetting(RpcServiceSetting setting) {
        this.setting = setting;
        return this;
    }

}

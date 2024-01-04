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
package com.tny.game.net.application;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;

import static com.tny.game.common.lifecycle.LifecycleLevel.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/7 3:38 下午
 */
public class NetBootstrap<S extends NetBootstrapSetting> implements ServedService, AppPrepareStart {

    protected S setting;

    protected NetIdGenerator idGenerator;

    private NetworkContext context;

    private final NetAppContext appContext;

    public NetBootstrap(NetAppContext appContext, S setting) {
        this.appContext = appContext;
        this.setting = setting;
    }

    public <T> NetworkContext getContext() {
        return as(this.context);
    }

    @Override
    public void prepareStart() throws Exception {
        MessageFactory messageFactory = UnitLoader.getLoader(MessageFactory.class).checkUnit(this.setting.getMessageFactory());
        ContactFactory contactFactory = UnitLoader.getLoader(ContactFactory.class).checkUnit(this.setting.getContactFactory());
        MessageDispatcher messageDispatcher = UnitLoader.getLoader(MessageDispatcher.class).checkUnit(this.setting.getMessageDispatcher());
        CommandExecutorFactory commandTaskProcessor =
                UnitLoader.getLoader(CommandExecutorFactory.class).checkUnit(this.setting.getCommandExecutorFactory());
        RpcForwarder rpcForwarder = UnitLoader.getLoader(RpcForwarder.class).checkUnit(this.setting.getRpcForwarder());
        RpcMonitor rpcMonitor = UnitLoader.getLoader(RpcMonitor.class).checkUnit();
        this.context = new NetBootstrapContext(appContext, this.setting, messageDispatcher, commandTaskProcessor,
                messageFactory, contactFactory, rpcForwarder, rpcMonitor);
        this.idGenerator = UnitLoader.getLoader(NetIdGenerator.class).checkUnit(this.setting.getTunnelIdGenerator());
        this.onLoadUnit(this.setting);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    public S getSetting() {
        return setting;
    }

    protected void onLoadUnit(S setting) {
    }

    @Override
    public String getService() {
        return setting.serviceName();
    }

    @Override
    public String getServeName() {
        return setting.discoverService();
    }
}

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
package com.tny.game.net.application;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

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
        SessionFactory sessionFactory = UnitLoader.getLoader(SessionFactory.class).checkUnit(this.setting.getSessionFactory());
        MessageDispatcher messageDispatcher = UnitLoader.getLoader(MessageDispatcher.class).checkUnit(this.setting.getMessageDispatcher());
        CommandExecutorFactory commandTaskProcessor =
                UnitLoader.getLoader(CommandExecutorFactory.class).checkUnit(this.setting.getCommandExecutorFactory());
        RpcForwarder rpcForwarder = UnitLoader.getLoader(RpcForwarder.class).checkUnit(this.setting.getRpcForwarder());
        RpcMonitor rpcMonitor = UnitLoader.getLoader(RpcMonitor.class).checkUnit();
        this.context = new NetBootstrapContext(appContext, this.setting, messageDispatcher, commandTaskProcessor,
                messageFactory, sessionFactory, contactFactory, rpcForwarder, rpcMonitor);
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

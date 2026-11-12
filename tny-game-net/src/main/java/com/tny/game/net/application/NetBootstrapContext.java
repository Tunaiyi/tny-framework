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

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/6 12:09 下午
 */
public class NetBootstrapContext implements NetworkContext {

    private final NetAppContext appContext;

    private final NetBootstrapSetting setting;

    private final MessageFactory messageFactory;

    private final SessionFactory sessionFactory;

    private final ContactFactory contactFactory;


    private final MessageDispatcher messageDispatcher;

    private final CommandExecutorFactory commandExecutorFactory;

    private final RpcForwarder rpcForwarder;

    private final RpcMonitor rpcMonitor;

    public NetBootstrapContext() {
        this.sessionFactory = new CommonSessionFactory();
        this.messageFactory = new CommonMessageFactory();
        this.contactFactory = new DefaultContactFactory();
        this.rpcMonitor = new RpcMonitor();
        this.appContext = null;
        this.setting = null;
        this.messageDispatcher = null;
        this.commandExecutorFactory = null;
        this.rpcForwarder = null;
    }

    public NetBootstrapContext(
            NetAppContext appContext,
            NetBootstrapSetting setting,
            MessageDispatcher messageDispatcher,
            CommandExecutorFactory commandExecutorFactory,
            MessageFactory messageFactory,
            SessionFactory sessionFactory,
            ContactFactory contactFactory,
            RpcForwarder rpcForwarder,
            RpcMonitor rpcMonitor) {
        this.appContext = appContext;
        this.setting = setting;
        this.messageDispatcher = messageDispatcher;
        this.commandExecutorFactory = commandExecutorFactory;
        this.messageFactory = messageFactory;
        this.sessionFactory = sessionFactory;
        this.contactFactory = contactFactory;
        this.rpcForwarder = rpcForwarder;
        this.rpcMonitor = rpcMonitor;
    }

    @Override
    public NetBootstrapSetting getSetting() {
        return setting;
    }

    @Override
    public MessageFactory getMessageFactory() {
        return this.messageFactory;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public ContactFactory getContactFactory() {
        return this.contactFactory;
    }

    @Override
    public MessageDispatcher getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public CommandExecutorFactory getCommandExecutorFactory() {
        return this.commandExecutorFactory;
    }

    @Override
    public RpcForwarder getRpcForwarder() {
        return rpcForwarder;
    }

    @Override
    public RpcMonitor getRpcMonitor() {
        return rpcMonitor;
    }

    @Override
    public NetAppContext getAppContext() {
        return appContext;
    }

}

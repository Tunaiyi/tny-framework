/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.base;

import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.rpc.*;

import static com.tny.game.common.utils.ObjectAide.*;

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

    private final MessagerFactory messagerFactory;

    private final CertificateFactory<?> certificateFactory;

    private final MessageDispatcher messageDispatcher;

    private final CommandTaskBoxProcessor commandTaskProcessor;

    private final RpcForwarder rpcForwarder;

    public NetBootstrapContext() {
        this.messageFactory = new CommonMessageFactory();
        this.messagerFactory = new DefaultMessagerFactory();
        this.certificateFactory = new DefaultCertificateFactory<>();
        this.appContext = null;
        this.setting = null;
        this.messageDispatcher = null;
        this.commandTaskProcessor = null;
        this.rpcForwarder = null;
    }

    public NetBootstrapContext(
            NetAppContext appContext,
            NetBootstrapSetting setting,
            MessageDispatcher messageDispatcher,
            CommandTaskBoxProcessor commandTaskProcessor,
            MessageFactory messageFactory,
            MessagerFactory messagerFactory,
            CertificateFactory<?> certificateFactory,
            RpcForwarder rpcForwarder) {
        this.appContext = appContext;
        this.setting = setting;
        this.messageDispatcher = messageDispatcher;
        this.commandTaskProcessor = commandTaskProcessor;
        this.messageFactory = messageFactory;
        this.messagerFactory = messagerFactory;
        this.certificateFactory = certificateFactory;
        this.rpcForwarder = rpcForwarder;
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
    public MessagerFactory getMessagerFactory() {
        return this.messagerFactory;
    }

    @Override
    public <I> CertificateFactory<I> getCertificateFactory() {
        return as(this.certificateFactory);
    }

    @Override
    public MessageDispatcher getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public CommandTaskBoxProcessor getCommandTaskProcessor() {
        return this.commandTaskProcessor;
    }

    @Override
    public RpcForwarder getRpcForwarder() {
        return rpcForwarder;
    }

    @Override
    public NetAppContext getAppContext() {
        return appContext;
    }

}

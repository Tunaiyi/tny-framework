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
package com.tny.game.net.application.configuration;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.common.lifecycle.unit.UnitNames.*;

public abstract class CommonNetBootstrapSetting implements NetBootstrapSetting {

    /**
     * 服务名
     */
    private String name;

    /**
     * 是否是转发
     */
    private boolean forwardable = false;

    /**
     * 服务发现: 服务名
     */
    private String serveName;

    /**
     * 消息分发起名气
     */
    private String messageDispatcher = defaultName(MessageDispatcher.class);

    private String commandExecutorFactory = defaultName(CommandExecutorFactory.class);

    private String messageFactory = defaultName(MessageFactory.class);

    private String contactFactory = defaultName(ContactFactory.class);

    private String tunnelIdGenerator = defaultName(NetIdGenerator.class);

    private String sessionFactory = defaultName(SessionFactory.class);

    private String rpcForwarder = defaultName(RpcForwarder.class);

    private Set<String> readIgnoreHeaders = new HashSet<>();

    private Set<String> writeIgnoreHeaders = new HashSet<>();

    protected CommonNetBootstrapSetting() {
    }

    @Override
    public String getMessageFactory() {
        return this.messageFactory;
    }

    @Override
    public String getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public String getContactFactory() {
        return contactFactory;
    }

    @Override
    public String getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public String getCommandExecutorFactory() {
        return this.commandExecutorFactory;
    }

    @Override
    public String getTunnelIdGenerator() {
        return tunnelIdGenerator;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getService() {
        return this.name;
    }

    @Override
    public boolean isForwardable() {
        return forwardable;
    }

    @Override
    public String getServeName() {
        return serveName;
    }

    @Override
    public String getRpcForwarder() {
        return rpcForwarder;
    }

    @Override
    public String serviceName() {
        return ifBlank(this.name, serveName);
    }

    public CommonNetBootstrapSetting setName(String name) {
        if (StringUtils.isBlank(this.name)) {
            this.name = name;
        }
        return this;
    }

    public CommonNetBootstrapSetting setServeName(String serveName) {
        this.serveName = serveName;
        return this;
    }

    public CommonNetBootstrapSetting setMessageFactory(String messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public CommonNetBootstrapSetting setContactFactory(String contactFactory) {
        this.contactFactory = contactFactory;
        return this;
    }


    public CommonNetBootstrapSetting setMessageDispatcher(String messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        return this;
    }

    public CommonNetBootstrapSetting setTunnelIdGenerator(String tunnelIdGenerator) {
        this.tunnelIdGenerator = tunnelIdGenerator;
        return this;
    }

    public CommonNetBootstrapSetting setCommandExecutorFactory(String commandExecutorFactory) {
        this.commandExecutorFactory = commandExecutorFactory;
        return this;
    }

    public CommonNetBootstrapSetting setRpcForwarder(String rpcForwarder) {
        this.rpcForwarder = rpcForwarder;
        return this;
    }

    public CommonNetBootstrapSetting setForwardable(boolean forwardable) {
        this.forwardable = forwardable;
        return this;
    }

    public CommonNetBootstrapSetting setSessionFactory(String sessionFactory) {
        this.sessionFactory = sessionFactory;
        return this;
    }

    @Override
    public Set<String> getReadIgnoreHeaders() {
        return readIgnoreHeaders;
    }

    @Override
    public Set<String> getWriteIgnoreHeaders() {
        return writeIgnoreHeaders;
    }

    public CommonNetBootstrapSetting setReadIgnoreHeaders(Set<String> readIgnoreHeaders) {
        this.readIgnoreHeaders = readIgnoreHeaders;
        return this;
    }

    public CommonNetBootstrapSetting setWriteIgnoreHeaders(Set<String> writeIgnoreHeaders) {
        this.writeIgnoreHeaders = writeIgnoreHeaders;
        return this;
    }

}

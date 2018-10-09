package com.tny.game.net.common;


import com.google.common.collect.ImmutableList;
import com.tny.game.common.config.*;
import com.tny.game.common.context.*;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import com.tny.game.net.utils.NetConfigs;

import java.io.IOException;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class AbstractAppConfiguration<T> implements AppConfiguration<T> {

    private String name;

    private String appType = "default";

    private String scopeType = "online";

    private Config config = ConfigLib.newConfig(new Properties());

    private Attributes attributes = ContextAttributes.create();

    protected T defaultUserId;

    protected SessionKeeperFactory sessionKeeperFactory;

    protected MessageFactory<T> messageBuilderFactory;

    protected MessageHandler<T> messageHandler;

    protected DispatchCommandExecutor dispatchCommandExecutor;

    protected MessageDispatcher messageDispatcher;

    protected ExprHolderFactory exprHolderFactory;

    protected AbstractAppConfiguration(String name, T defaultUserId) {
        this.name = name;
    }

    protected AbstractAppConfiguration(String name, T defaultUserId, String path) throws IOException {
        this(name, defaultUserId, ImmutableList.of(path));
    }

    protected AbstractAppConfiguration(String name, T defaultUserId, List<String> paths) throws IOException {
        this.name = name;
        this.defaultUserId = defaultUserId;
        this.setConfig(paths.toArray(new String[0]));
    }

    @Override
    public T getDefaultUserId() {
        return defaultUserId;
    }

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public String getScopeType() {
        return scopeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Attributes attributes() {
        return this.attributes;
    }

    @Override
    public Config getProperties() {
        return config;
    }

    @Override
    public MessageFactory<T> getMessageFactory() {
        return as(messageBuilderFactory);
    }

    @Override
    public MessageHandler<T> getMessageHandler() {
        return as(messageHandler);
    }

    @Override
    public DispatchCommandExecutor getDispatchCommandExecutor() {
        return dispatchCommandExecutor;
    }

    @Override
    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }

    @Override
    public ExprHolderFactory getExprHolderFactory() {
        return exprHolderFactory;
    }


    public AbstractAppConfiguration setConfig(Map<String, Object> config) {
        loadConfig(ConfigLib.newConfig(config, InetSocketAddressConfigFormatter.FORMATTER));
        return this;
    }

    public AbstractAppConfiguration setConfig(Properties properties) {
        loadConfig(ConfigLib.newConfig(properties, InetSocketAddressConfigFormatter.FORMATTER));
        return this;
    }

    public AbstractAppConfiguration setConfig(String... paths) throws IOException {
        Properties properties = new Properties();
        for (String path : paths)
            properties.load(ConfigLoader.loadInputStream(path));
        loadConfig(ConfigLib.newConfig(properties, InetSocketAddressConfigFormatter.FORMATTER));
        return this;
    }

    private void loadConfig(Config config) {
        this.config = config;
        this.appType = this.config.getStr(NetConfigs.SERVER_APP_TYPE, this.appType);
        this.scopeType = this.config.getStr(NetConfigs.SERVER_SCOPE_TYPE, this.scopeType);
    }

}

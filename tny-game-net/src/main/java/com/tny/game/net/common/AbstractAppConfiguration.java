package com.tny.game.net.common;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.config.Config;
import com.tny.game.common.config.ConfigLib;
import com.tny.game.common.config.ConfigLoader;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.MessageDispatcher;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.session.SessionInputEventHandler;
import com.tny.game.net.session.SessionOutputEventHandler;
import com.tny.game.net.session.holder.NetSessionHolder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractAppConfiguration implements AppConfiguration {

    private String name = "";

    private String appType = "default";

    private String scopeType = "online";

    private Config config = ConfigLib.newConfig(new Properties());

    private Attributes attributes = ContextAttributes.create();

    protected NetSessionHolder sessionHolder;

    protected SessionFactory sessionFactory;

    protected MessageBuilderFactory messageBuilderFactory;

    protected SessionInputEventHandler inputEventHandler;

    protected SessionOutputEventHandler outputEventHandler;

    protected DispatchCommandExecutor dispatchCommandExecutor;

    protected MessageDispatcher messageDispatcher;

    protected MessageSignGenerator messageSignGenerator;

    protected AbstractAppConfiguration(String name) {
        this.name = name;
    }

    protected AbstractAppConfiguration(String name, String path) throws IOException {
        this(name, ImmutableList.of(path));
    }

    protected AbstractAppConfiguration(String name, List<String> paths) throws IOException {
        this.name = name;
        this.setConfig(paths.toArray(new String[paths.size()]));
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
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public NetSessionHolder getSessionHolder() {
        return sessionHolder;
    }

    @Override
    public MessageBuilderFactory getMessageBuilderFactory() {
        return messageBuilderFactory;
    }

    @Override
    public SessionOutputEventHandler getOutputEventHandler() {
        return outputEventHandler;
    }

    @Override
    public SessionInputEventHandler getInputEventHandler() {
        return inputEventHandler;
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
    public MessageSignGenerator getMessageSignGenerator() {
        return messageSignGenerator;
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

    protected void loadConfig(Config config) {
        this.config = config;
        this.appType = this.config.getStr(AppConstants.APP_TYPE, this.appType);
        this.scopeType = this.config.getStr(AppConstants.SCOPE_TYPE, this.scopeType);
    }


}

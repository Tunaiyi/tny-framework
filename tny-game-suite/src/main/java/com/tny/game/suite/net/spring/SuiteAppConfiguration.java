package com.tny.game.suite.net.spring;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.expr.groovy.GroovyExprHolderFactory;
import com.tny.game.net.base.AppUtils;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.common.AbstractAppConfiguration;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.netty.NettyAppConfiguration;
import com.tny.game.net.netty.coder.ChannelMaker;
import com.tny.game.net.session.*;
import com.tny.game.net.session.NetSessionHolder;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认服务器上下文对象
 * Created by Kun Yang on 16/1/27.
 */
// @Component
// @Profile({GAME, SERVER})
public class SuiteAppConfiguration extends AbstractAppConfiguration implements NettyAppConfiguration, ApplicationContextAware, ServerPrepareStart {

    private ApplicationContext applicationContext;

    private ChannelMaker channelMaker;

    private String sessionHolderName;

    private String sessionFactoryName;

    private String messageBuilderFactoryName;

    private String inputEventHandlerName;

    private String outputEventHandlerName;

    private String dispatchCommandExecutorName;

    private String messageDispatcherName;

    private String channelMakerName;

    private String exprHolderFactoryName;

    public SuiteAppConfiguration(String name) {
        super(name);
    }

    public SuiteAppConfiguration(String name, String path) throws IOException {
        super(name, path);
    }

    public SuiteAppConfiguration(String name, List<String> paths) throws IOException {
        super(name, paths);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChannelMaker<Channel> getChannelMaker() {
        return this.channelMaker;
    }


    public void setSessionHolder(String sessionHolder) {
        this.sessionHolderName = sessionHolder;
    }

    public void setSessionFactory(String sessionFactory) {
        this.sessionFactoryName = sessionFactory;
    }

    public void setMessageBuilderFactory(String messageBuilderFactory) {
        this.messageBuilderFactoryName = messageBuilderFactory;
    }

    public void setInputEventHandler(String inputEventHandler) {
        this.inputEventHandlerName = inputEventHandler;
    }

    public void setOutputEventHandler(String outputEventHandler) {
        this.outputEventHandlerName = outputEventHandler;
    }

    public void setDispatchCommandExecutor(String dispatchCommandExecutor) {
        this.dispatchCommandExecutorName = dispatchCommandExecutor;
    }

    public void setMessageDispatcher(String messageDispatcher) {
        this.messageDispatcherName = messageDispatcher;
    }

    public void setChannelMaker(String channelMaker) {
        this.channelMakerName = channelMaker;
    }

    public void setExprHolderFactoryName(String exprHolderFactoryName) {
        this.exprHolderFactoryName = exprHolderFactoryName;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() throws Exception {
        this.sessionHolder = load(NetSessionHolder.class, this.sessionHolderName);
        this.sessionFactory = load(SessionFactory.class, this.sessionFactoryName);
        this.messageBuilderFactory = load(MessageBuilderFactory.class, this.messageBuilderFactoryName);
        this.inputEventHandler = load(MessageInputEventHandler.class, this.inputEventHandlerName);
        this.outputEventHandler = load(MessageOutputEventHandler.class, this.outputEventHandlerName);
        this.dispatchCommandExecutor = load(DispatchCommandExecutor.class, this.dispatchCommandExecutorName);
        this.messageDispatcher = load(MessageDispatcher.class, this.messageDispatcherName);
        this.channelMaker = load(ChannelMaker.class, this.channelMakerName);
        if (StringUtils.isBlank(this.exprHolderFactoryName)) {
            this.exprHolderFactory = new GroovyExprHolderFactory();
        } else {
            this.exprHolderFactory = load(ExprHolderFactory.class, this.exprHolderFactoryName);
        }
    }

    private <T> T load(Class<T> clazz, String checkUnit) {
        if (StringUtils.isBlank(checkUnit))
            return null;
        Map<String, T> beans = applicationContext.getBeansOfType(clazz);
        Map<String, T> units = ImmutableMap.copyOf(beans.values()
                .stream()
                .collect(Collectors.toMap(
                        AppUtils::getUnitName,
                        ObjectAide::self
                )));
        return Throws.checkNotNull(units.get(checkUnit), "{} [{}] 不存在", clazz, checkUnit);
    }


}
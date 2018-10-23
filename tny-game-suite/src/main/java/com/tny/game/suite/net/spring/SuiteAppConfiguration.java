package com.tny.game.suite.net.spring;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.expr.groovy.GroovyExprHolderFactory;
import com.tny.game.net.base.AppUtils;
import com.tny.game.net.command.executor.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.base.configuration.AbstractAppConfiguration;
import com.tny.game.net.session.*;
import com.tny.game.net.message.MessageFactory;
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
public class SuiteAppConfiguration extends AbstractAppConfiguration implements ApplicationContextAware, ServerPrepareStart {

    private ApplicationContext applicationContext;

    private String sessionKeeperFactoryName;

    private String sessionFactoryName;

    private String messageBuilderFactoryName;

    private String inputEventHandlerName;

    private String outputEventHandlerName;

    private String dispatchCommandExecutorName;

    private String messageDispatcherName;

    private String exprHolderFactoryName;

    public SuiteAppConfiguration(String name, Object defaultUserId) {
        super(name, defaultUserId);
    }

    public SuiteAppConfiguration(String name, Object defaultUserId, String path) throws IOException {
        super(name, defaultUserId, path);
    }

    public SuiteAppConfiguration(String name, Object defaultUserId, List<String> paths) throws IOException {
        super(name, defaultUserId, paths);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setSessionHolder(String sessionHolder) {
        this.sessionKeeperFactoryName = sessionHolder;
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

    public void setExprHolderFactoryName(String exprHolderFactoryName) {
        this.exprHolderFactoryName = exprHolderFactoryName;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        this.sessionKeeperFactory = load(SessionKeeperMannager.class, this.sessionKeeperFactoryName);
        this.sessionFactory = load(SessionFactory.class, this.sessionFactoryName);
        this.messageBuilderFactory = load(MessageFactory.class, this.messageBuilderFactoryName);
        this.messageHandler = load(MessageInputEventHandler.class, this.inputEventHandlerName);
        this.outputEventHandler = load(MessageOutputEventHandler.class, this.outputEventHandlerName);
        this.dispatchCommandExecutor = load(DispatchCommandExecutor.class, this.dispatchCommandExecutorName);
        this.messageDispatcher = load(MessageDispatcher.class, this.messageDispatcherName);
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
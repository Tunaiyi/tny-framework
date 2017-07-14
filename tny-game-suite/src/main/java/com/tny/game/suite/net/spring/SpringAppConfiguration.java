package com.tny.game.suite.net.spring;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.utils.Throws;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.common.lifecycle.LifecycleLevel;
import com.tny.game.common.lifecycle.PrepareStarter;
import com.tny.game.common.lifecycle.ServerPrepareStart;
import com.tny.game.net.base.AppUtils;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.common.AbstractAppConfiguration;
import com.tny.game.net.common.dispatcher.MessageDispatcher;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.netty.NettyAppConfiguration;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.session.event.SessionInputEventHandler;
import com.tny.game.net.session.event.SessionOutputEventHandler;
import com.tny.game.net.session.holder.NetSessionHolder;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认服务器上下文对象
 * Created by Kun Yang on 16/1/27.
 */
// @Component
// @Profile({GAME, SERVER})
public class SpringAppConfiguration extends AbstractAppConfiguration implements NettyAppConfiguration, ApplicationContextAware, ServerPrepareStart {

    private ApplicationContext applicationContext;

    private ChannelMaker channelMaker;

    private String sessionHolderName;

    private String sessionFactoryName;

    private String messageBuilderFactoryName;

    private String inputEventHandlerName;

    private String outputEventHandlerName;

    private String dispatchCommandExecutorName;

    private String messageDispatcherName;

    private String messageSignGeneratorName;

    private String channelMakerName;

    public SpringAppConfiguration(String name) {
        super(name);
    }

    public SpringAppConfiguration(String name, String path) throws IOException {
        super(name, path);
    }

    public SpringAppConfiguration(String name, List<String> paths) throws IOException {
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

    public void setMessageSignGenerator(String messageSignGenerator) {
        this.messageSignGeneratorName = messageSignGenerator;
    }

    public void setChannelMaker(String channelMaker) {
        this.channelMakerName = channelMaker;
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
        this.inputEventHandler = load(SessionInputEventHandler.class, this.inputEventHandlerName);
        this.outputEventHandler = load(SessionOutputEventHandler.class, this.outputEventHandlerName);
        this.dispatchCommandExecutor = load(DispatchCommandExecutor.class, this.dispatchCommandExecutorName);
        this.messageDispatcher = load(MessageDispatcher.class, this.messageDispatcherName);
        this.messageSignGenerator = load(MessageSignGenerator.class, this.messageSignGeneratorName);
        this.channelMaker = load(ChannelMaker.class, this.channelMakerName);
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
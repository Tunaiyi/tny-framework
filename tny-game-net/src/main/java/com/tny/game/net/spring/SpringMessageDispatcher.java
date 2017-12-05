package com.tny.game.net.spring;

import com.tny.game.common.lifecycle.LifecycleLevel;
import com.tny.game.common.lifecycle.PrepareStarter;
import com.tny.game.common.lifecycle.ServerPrepareStart;
import com.tny.game.net.annotation.Controller;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.suite.app.AppConfiguration;
import com.tny.game.suite.app.annotation.Unit;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.checker.ControllerChecker;
import com.tny.game.net.command.dispatcher.CommonMessageDispatcher;
import com.tny.game.net.command.listener.DispatchCommandListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

@Unit("SpringMessageDispatcher")
public final class SpringMessageDispatcher extends CommonMessageDispatcher implements ApplicationContextAware, ServerPrepareStart {

    private ApplicationContext applicationContext;

    public SpringMessageDispatcher(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }


    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() throws Exception {
        final Map<String, AuthProvider> providerMap = this.applicationContext.getBeansOfType(AuthProvider.class);
        this.addAuthProvider(providerMap.values());
        final Map<String, ControllerChecker> checkerMap = this.applicationContext.getBeansOfType(ControllerChecker.class);
        this.addControllerChecker(checkerMap.values());
        final Map<String, ControllerPlugin> pluginMap = this.applicationContext.getBeansOfType(ControllerPlugin.class);
        this.addControllerPlugin(pluginMap.values());
        final Map<String, Object> handlerMap = this.applicationContext.getBeansWithAnnotation(Controller.class);
        this.addController(handlerMap.values());
        final Map<String, DispatchCommandListener> listenerMap = this.applicationContext.getBeansOfType(DispatchCommandListener.class);
        this.addListener(listenerMap.values());
    }

}

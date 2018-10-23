package com.tny.game.suite.net.spring;

import com.tny.game.common.lifecycle.*;
import com.tny.game.net.annotation.Controller;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.plugins.ControllerPlugin;
import com.tny.game.net.command.auth.AuthenticateValidator;
import com.tny.game.net.command.dispatcher.CommonMessageDispatcher;
import com.tny.game.net.command.listener.DispatchCommandListener;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.Map;

@Unit("SuiteMessageDispatcher")
public final class SuiteMessageDispatcher extends CommonMessageDispatcher implements ApplicationContextAware, ServerPrepareStart {

    private ApplicationContext applicationContext;

    public SuiteMessageDispatcher(AppConfiguration appConfiguration) {
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
        final Map<String, AuthenticateValidator> providerMap = this.applicationContext.getBeansOfType(AuthenticateValidator.class);
        this.addAuthProvider(providerMap.values());
        // final Map<String, ControllerChecker> checkerMap = this.applicationContext.getBeansOfType(ControllerChecker.class);
        // this.addControllerChecker(checkerMap.values());
        final Map<String, ControllerPlugin> pluginMap = this.applicationContext.getBeansOfType(ControllerPlugin.class);
        this.addControllerPlugin(pluginMap.values());
        final Map<String, Object> handlerMap = this.applicationContext.getBeansWithAnnotation(Controller.class);
        this.addController(handlerMap.values());
        final Map<String, DispatchCommandListener> listenerMap = this.applicationContext.getBeansOfType(DispatchCommandListener.class);
        this.addListener(listenerMap.values());
    }

}

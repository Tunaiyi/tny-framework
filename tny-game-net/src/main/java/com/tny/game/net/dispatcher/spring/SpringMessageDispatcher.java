package com.tny.game.net.dispatcher.spring;

import com.tny.game.annotation.Controller;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.dispatcher.DefaultMessageDispatcher;
import com.tny.game.net.dispatcher.listener.DispatcherRequestListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public final class SpringMessageDispatcher extends DefaultMessageDispatcher implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public SpringMessageDispatcher(boolean checkTimeOut) {
        super(checkTimeOut);
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    @Override
    public void initDispatcher(AppContext appContext) {
        final Map<String, Object> handlerMap = this.applicationContext.getBeansWithAnnotation(Controller.class);
        this.addController(handlerMap.values());
        super.initDispatcher(appContext);
        final Map<String, DispatcherRequestListener> listenerMap = this.applicationContext.getBeansOfType(DispatcherRequestListener.class);
        this.listeners.addAll(listenerMap.values());
    }

}

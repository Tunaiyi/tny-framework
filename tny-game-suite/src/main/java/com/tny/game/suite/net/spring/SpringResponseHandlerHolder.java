package com.tny.game.suite.net.spring;

import com.tny.game.net.dispatcher.ResponseHandler;
import com.tny.game.net.dispatcher.ResponseHandlerHolder;
import com.tny.game.lifecycle.PrepareStarter;
import com.tny.game.lifecycle.ServerPrepareStart;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kun Yang on 16/8/26.
 */
@Component
public class SpringResponseHandlerHolder extends ResponseHandlerHolder implements ApplicationContextAware, ServerPrepareStart {

    @Autowired
    private ApplicationContext context;

    @Override
    public void prepareStart() throws Exception {
        List<ResponseHandler> handlers = new ArrayList<>(this.context.getBeansOfType(ResponseHandler.class).values());
        handlers.forEach(this::register);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}

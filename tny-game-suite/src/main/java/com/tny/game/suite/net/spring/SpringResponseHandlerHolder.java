package com.tny.game.suite.net.spring;

import com.tny.game.net.dispatcher.ResponseHandler;
import com.tny.game.net.dispatcher.ResponseHandlerHolder;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
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
public class SpringResponseHandlerHolder extends ResponseHandlerHolder implements ApplicationContextAware, ServerPreStart {

    @Autowired
    private ApplicationContext context;

    @Override
    public void initialize() throws Exception {
        List<ResponseHandler> handlers = new ArrayList<>(this.context.getBeansOfType(ResponseHandler.class).values());
        handlers.forEach(this::register);
    }

    @Override
    public PerIniter getIniter() {
        return PerIniter.initer(this.getClass());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}

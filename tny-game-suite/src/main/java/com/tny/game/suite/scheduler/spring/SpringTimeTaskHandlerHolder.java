package com.tny.game.suite.scheduler.spring;

import com.tny.game.scheduler.AbstractTimeTaskHandlerHolder;
import com.tny.game.scheduler.TimeTaskHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.tny.game.suite.SuiteProfiles.*;

@Component("timeTaskHandlerHolder")
@Profile({SCHEDULER, GAME})
public class SpringTimeTaskHandlerHolder extends AbstractTimeTaskHandlerHolder implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void init() {
        Map<String, TimeTaskHandler> handlerMap = this.applicationContext.getBeansOfType(TimeTaskHandler.class);
        for (TimeTaskHandler handler : handlerMap.values()) {
            this.handlerHashMap.put(handler.getHandlerName(), handler);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

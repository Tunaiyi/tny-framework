package com.tny.game.suite.launcher;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kun Yang on 16/5/31.
 */
public class SpringServerIniterListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private ApplicationContext context;

    private AtomicBoolean inited = new AtomicBoolean(false);

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (inited.compareAndSet(false, true)) {
            ServerIniterProcessor processor = new ServerIniterProcessor();
            processor.setApplicationContext(this.context);
            try {
                processor.initPreStart();
                processor.initPostStart();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

}

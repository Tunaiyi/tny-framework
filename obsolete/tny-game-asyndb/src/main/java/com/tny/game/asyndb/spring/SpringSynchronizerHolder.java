package com.tny.game.asyndb.spring;

import com.tny.game.asyndb.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringSynchronizerHolder implements SynchronizerHolder, ApplicationContextAware {

    private ApplicationContext context;

    private final Map<Class<?>, Synchronizer<Object>> synchronizerMap = new ConcurrentHashMap<Class<?>, Synchronizer<Object>>();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Synchronizer<Object> getSynchronizer(Class<? extends Synchronizer> syncClass) {
        Synchronizer<Object> synchronizer = this.synchronizerMap.get(syncClass);
        if (synchronizer == null) {
            synchronizer = this.context.getBean(syncClass);
            if (synchronizer != null) {
                this.synchronizerMap.put(syncClass, synchronizer);
            }
        }
        return synchronizer;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}

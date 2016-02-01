package com.tny.game.asyndb.spring;

import com.tny.game.asyndb.Synchronizer;
import com.tny.game.asyndb.SynchronizerHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringSynchronizerHolder implements SynchronizerHolder, ApplicationContextAware {

    private ApplicationContext context;

    private final Map<Class<?>, Synchronizer<Object>> synchronizerMap = new ConcurrentHashMap<Class<?>, Synchronizer<Object>>();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Synchronizer<Object> getSynchronizer(Class<? extends Synchronizer> synchClass) {
        Synchronizer<Object> synchronizer = this.synchronizerMap.get(synchClass);
        if (synchronizer == null) {
            synchronizer = this.context.getBean(synchClass);
            if (synchronizer != null) {
                this.synchronizerMap.put(synchClass, synchronizer);
            }
        }
        return synchronizer;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}

package com.tny.game.boot.registrar;

import com.tny.game.common.event.bus.*;
import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.common.lifecycle.*;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.*;

public class EventListenerInitiator implements AppPrepareStart {

    @Resource
    private ApplicationContext context;

    @Override
    public void prepareStart() {
        Map<String, Object> listenerMap = this.context.getBeansWithAnnotation(EventListener.class);
        List<Object> listenerList = new ArrayList<>(listenerMap.values());
        listenerList.sort((o1, o2) -> {
            EventListener l1 = o1.getClass().getAnnotation(EventListener.class);
            EventListener l2 = o2.getClass().getAnnotation(EventListener.class);
            return l1.level() - l2.level();
        });
        listenerList.forEach(GlobalListenerHolder.getInstance()::addListener);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

}
package com.tny.game.boot.initiator;

import com.tny.game.common.event.bus.*;
import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.common.lifecycle.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class EventListenerInitiator implements AppPrepareStart {

    @Resource
    private ApplicationContext context;

    @Override
    public void prepareStart() {
        Map<String, Object> listenerMap = this.context.getBeansWithAnnotation(Listener.class);
        List<Object> listenerList = new ArrayList<>(listenerMap.values());
        listenerList.sort((o1, o2) -> {
            Listener l1 = o1.getClass().getAnnotation(Listener.class);
            Listener l2 = o2.getClass().getAnnotation(Listener.class);
            return l1.level() - l2.level();
        });
        listenerList.forEach(GlobalListenerHolder.getInstance()::addListener);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

}
package com.tny.game.boot.registrar;

import com.tny.game.boot.event.annotation.*;
import com.tny.game.common.event.bus.*;
import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.common.lifecycle.*;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

public class EventListenerInitiator implements AppPrepareStart {

    @Resource
    private ApplicationContext context;

    private static class GlobalListener {

        private final GlobalEventListener listener;

        private final Object object;

        private GlobalListener(Object object) {
            this.object = object;
            this.listener = loadEventHandler(object.getClass());
        }

        private GlobalEventListener getListener() {
            return listener;
        }

        private Object getObject() {
            return object;
        }

    }

    @Override
    public void prepareStart() {
        Map<String, Object> listenerMap = this.context.getBeansWithAnnotation(GlobalEventListener.class);
        Map<String, Object> handlerMap = this.context.getBeansWithAnnotation(EventHandler.class);
        Set<Object> set = new HashSet<>(listenerMap.values());
        set.addAll(handlerMap.values());
        List<Object> listeners = set.stream()
                .map(GlobalListener::new)
                .filter((l) -> l.getListener() != null)
                .sorted((o1, o2) -> {
                    GlobalEventListener l1 = o1.getListener();
                    GlobalEventListener l2 = o2.getListener();
                    return l1.level() - l2.level();
                })
                .map(GlobalListener::getObject)
                .collect(Collectors.toList());
        listeners.forEach(GlobalListenerHolder.getInstance()::addListener);
    }

    private static GlobalEventListener loadEventHandler(Class<?> listener) {
        GlobalEventListener handler = listener.getAnnotation(GlobalEventListener.class);
        if (handler != null) {
            return handler;
        }
        EventHandler eventBean = listener.getAnnotation(EventHandler.class);
        if (eventBean != null) {
            return eventBean.listener();
        }
        return null;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

}
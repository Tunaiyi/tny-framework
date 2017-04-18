package com.tny.game.suite.initer;

import com.tny.game.event.GlobalListenerHolder;
import com.tny.game.event.annotation.Listener;
import com.tny.game.lifecycle.LifecycleLevel;
import com.tny.game.lifecycle.PrepareStarter;
import com.tny.game.lifecycle.ServerPrepareStart;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class EventDispatcherIniter implements ApplicationContextAware, ServerPrepareStart {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void prepareStart() throws Exception {
        Map<String, Object> listenerMap = this.context.getBeansWithAnnotation(Listener.class);
        List<Object> listenerList = new ArrayList<>(listenerMap.values());
        Collections.sort(listenerList, (o1, o2) -> {
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
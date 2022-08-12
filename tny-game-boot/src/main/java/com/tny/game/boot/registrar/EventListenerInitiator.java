/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_MAX);
    }

}
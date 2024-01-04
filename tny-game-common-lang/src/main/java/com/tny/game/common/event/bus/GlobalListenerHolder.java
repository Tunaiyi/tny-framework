/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.event.bus;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * @author KGTny
 * @ClassName: ListenerHandlerHolder
 * @Description: 监听器处理器持有器
 * @date 2011-9-21 ����11:58:09
 * <p>
 * 监听器处理器持有器
 * <p>
 * 监听器处理器持有器,负责管理监听器处理方法<br>
 */
public class GlobalListenerHolder {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.EVENT);

    private static final GlobalListenerHolder holder = new GlobalListenerHolder();

    private final Map<Class<?>, List<?>> listenerMap = new CopyOnWriteMap<>();

    private GlobalListenerHolder() {
    }

    public static GlobalListenerHolder getInstance() {
        return holder;
    }

    public void addListener(Object listener) {
        for (Class<?> clazz : getAllClasses(listener.getClass())) {
            List<Object> listeners = getOrCreate(clazz);
            listeners.add(listener);
        }
    }

    public void removeListener(Object listener) {
        for (Class<?> clazz : getAllClasses(listener.getClass())) {
            List<Object> listeners = as(this.listenerMap.get(clazz));
            if (listeners != null) {
                listeners.add(listener);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <L> List<L> getListeners(Class<?> clazz) {
        List<?> listeners = this.listenerMap.get(clazz);
        if (listeners == null) {
            return Collections.emptyList();
        }
        return (List<L>) listeners;
    }

    private Set<Class<?>> getAllClasses(Class<?> clazz) {
        Set<Class<?>> classes = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == Object.class) {
            return classes;
        }
        classes.addAll(getAllClasses(superClass));
        return classes;
    }

    @SuppressWarnings("unchecked")
    private <T> List<Object> getOrCreate(Class<T> clazz) {
        List<?> listeners = this.listenerMap.get(clazz);
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            this.listenerMap.put(clazz, listeners);
        }
        return (List<Object>) listeners;
    }

}

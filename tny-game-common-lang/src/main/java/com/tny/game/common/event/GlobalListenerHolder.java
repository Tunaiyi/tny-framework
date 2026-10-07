/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.common.event;

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
                // 原实现误写 add：移除变追加（复制粘贴缺陷）
                listeners.remove(listener);
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
        // 原子创建（原 check-then-act 并发首注册各建各表互相覆盖）
        return (List<Object>) this.listenerMap.computeIfAbsent(clazz, k -> new CopyOnWriteArrayList<>());
    }

}

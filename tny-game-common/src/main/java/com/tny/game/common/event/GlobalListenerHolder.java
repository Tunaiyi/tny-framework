package com.tny.game.common.event;

import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.utils.Logs;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private static final Logger LOG = LoggerFactory.getLogger(Logs.EVENT);

    private Map<Class<?>, List<?>> listenerMap = new CopyOnWriteMap<>();

    private static GlobalListenerHolder holder = new GlobalListenerHolder();

    private GlobalListenerHolder() {
    }

    public static final GlobalListenerHolder getInstance() {
        return holder;
    }

    public void addListener(Object listener) {
        for (Class<?> clazz : getAllClasses(listener.getClass())) {
            List<Object> listeners = getOrCreate(clazz);
            listeners.add(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <L> List<L> getListeners(Class<?> clazz) {
        List<?> listeners = listenerMap.get(clazz);
        if (listeners == null)
            return Collections.emptyList();
        return (List<L>) listeners;
    }

    private Set<Class<?>> getAllClasses(Class<?> clazz) {
        Set<Class<?>> classes = new HashSet<>();
        classes.addAll(Arrays.asList(clazz.getInterfaces()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == Object.class)
            return classes;
        classes.addAll(getAllClasses(superClass));
        return classes;
    }

    @SuppressWarnings("unchecked")
    private <T> List<Object> getOrCreate(Class<T> clazz) {
        List<?> listeners = listenerMap.get(clazz);
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            listenerMap.put(clazz, listeners);
        }
        return (List<Object>) listeners;
    }

}

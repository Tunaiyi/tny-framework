package com.tny.game.event;

import com.tny.game.common.reflect.GClass;
import com.tny.game.common.reflect.GMethod;
import com.tny.game.common.reflect.MethodFilter;
import com.tny.game.common.reflect.ReflectUtils;
import com.tny.game.common.reflect.javassist.JSsistUtils;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.event.annotation.Listener;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author KGTny
 * @ClassName: EventDispatcher
 * @Description: 事件派发器
 * @date 2011-9-21 ����11:34:18
 * <p>
 * 事件派发器
 * <p>
 * 将事件派发到对应的EventListener监听器<br>
 */
public class EventDispatcher {

    /**
     * EventDispatcher Map
     */
    private final static EventDispatcher DISPATCHER = new EventDispatcher("DEFAULT");

    // private final static Logger LOGGER =
    private String name;

    /**
     * listener持有器Map
     *
     * @uml.property name="holderMap"
     * @uml.associationEnd qualifier=
     * "clazz:java.lang.Class cndw.framework.event.ListenerHolder"
     */
    private final Map<Class<?>, ListenerHolder> holderMap = new CopyOnWriteMap<Class<?>, ListenerHolder>();

    private EventDispatcher(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * 获取默认EventDispatcher
     * <p>
     * <p>
     * 获取默认的EventDispatcher<br>
     *
     * @param classes 指定的类型
     * @return EventDispatcher
     */
    public static EventDispatcher getDispatcher() {
        return DISPATCHER;
    }

    public static EventDispatcher createDispatcher(String name) {
        return new EventDispatcher(name);
    }

    /**
     * 派发指定的事件
     * <p>
     * <p>
     * 派发指定的事件到监听的监听器<br>
     *
     * @param event 派发的事件
     */
    public <E extends Event<?>> void dispatch(E event) {
        if (event == null)
            throw new NullPointerException("dispatcher dispatch event is null");
        if (this != DISPATCHER)
            DISPATCHER.dispatch(event);
        //		for (Class<?> clazz : holderMap.keySet()) {
        //			if (!clazz.isInstance(event))
        //				continue;
        ListenerHolder holder = this.holderMap.get(event.getClass());
        if (holder == null)
            return;
        List<ListenerHandlerHolder> handlerHolderList = holder.getListenerHandlerHolder(event.getHandler());
        if (handlerHolderList == null)
            return;
        for (ListenerHandlerHolder handlerHolder : handlerHolderList)
            handlerHolder.handle(event);
        //		}
    }

    private static final MethodFilter FILTER = new MethodFilter() {

        @Override
        public boolean filter(Method method) {
            Class<?>[] classes = method.getParameterTypes();
            return classes.length != 1 || !Event.class.isAssignableFrom(classes[0]) || !method.getName().startsWith("handle");
        }

    };

    /**
     * 添加监听器
     * <p>
     * <p>
     * 添加监听器<br>
     *
     * @param listener 添加的监听器
     */
    public void addListener(Object listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");
        // LOGGER.info("添加监听器: " + listener.getClass().toString());
        for (Class<?> interfaceClass : ReflectUtils.getDeepInterface(listener.getClass())) {
            GClass clazz = JSsistUtils.getGClass(interfaceClass, FILTER);
            for (GMethod method : clazz.getGMethodList()) {
                Method javaMethod = method.getJavaMethod();
                try {
                    Method listenerMethod = listener.getClass().getMethod(javaMethod.getName(), javaMethod.getParameterTypes());
                    Listener annotation = listenerMethod.getAnnotation(Listener.class);
                    if (annotation == null)
                        continue;
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                // 获取处理方法所处理事件类型
                Class<?> eventClass = this.getEventClass(method.getJavaMethod());
                ListenerHolder holder = null;
                // 获取监听器持有器
                holder = this.holderMap.get(eventClass);
                // 若无创建监听器持有器
                if (holder == null) {
                    synchronized (this.holderMap) {
                        holder = this.holderMap.get(eventClass);
                        if (holder == null) {
                            holder = new ListenerHolder();
                            this.holderMap.put(eventClass, holder);
                        }
                    }
                }
                if (holder != null) {
                    // 添加监听器处理器到监听器持有器
                    holder.addListenerHandlerHolder(new ListenerHandlerHolder(method, listener));
                }
            }

        }
    }

    /**
     * 移除指定监听器
     * <p>
     * <p>
     * 移除指定监听器<br>
     *
     * @param listener 指定监听器
     */
    public void removeListener(Object listener) {
        for (Entry<Class<?>, ListenerHolder> entry : this.holderMap.entrySet())
            entry.getValue().removeListenerHandlerHolder(listener);
    }

    /**
     * 添加监听器集合
     * <p>
     * <p>
     * 添加监听器集合<br>
     *
     * @param listenerCollection 添加的监听器集合
     */
    public void addAllListener(Collection<Object> listenerCollection) {
        for (Object listener : listenerCollection)
            this.addListener(listener);
    }

    /**
     * 移除所有监听器
     * <p>
     * <p>
     * 移除所有监听器<br>
     */
    public void clearListener() {
        this.holderMap.clear();
    }

    private Class<?> getEventClass(Method method) {
        return method.getParameterTypes()[0];
    }

}

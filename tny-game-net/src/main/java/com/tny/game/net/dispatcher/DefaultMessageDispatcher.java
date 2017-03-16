package com.tny.game.net.dispatcher;

import com.google.common.collect.ImmutableMap;
import com.tny.game.LogUtils;
import com.tny.game.common.utils.collection.CollectUtils;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.MessageMode;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import com.tny.game.net.session.Session;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultMessageDispatcher extends NetMessageDispatcher {

    private List<Object> controllers = new ArrayList<>();

    protected boolean checkMethods(final Method method) {
        final Class<?> clazz = method.getReturnType();
        if (!clazz.equals(Response.class) && !clazz.equals(Void.class))
            return false;
        final Class<?>[] clazzs = method.getParameterTypes();
        if (clazzs.length > 0 && clazzs[0].equals(Session.class))
            return false;
        return true;
    }

    public void addController(Object... controllers) {
        for (Object c : controllers)
            this.controllers.add(c);
    }

    public void addController(Collection<Object> controller) {
        this.controllers.addAll(controller);
    }

    @Override
    public void initDispatcher(AppContext appContext) {
        PluginHolder pluginHolder = appContext.getPluginHolder();
        if (pluginHolder == null)
            throw new NullPointerException("pluginHolder is null");
        AbstractNetSessionHolder sessionHolder = appContext.getSessionHolder();
        if (sessionHolder == null)
            throw new NullPointerException("sessionHolder is null");
        Map<Class<?>, ControllerChecker> checkerMap = appContext.getControllerCheckers()
                .stream()
                .collect(CollectUtils.toMap(ControllerChecker::getClass));
        Map<Integer, Map<MessageMode, MethodControllerHolder>> methodHolder = new HashMap<>();
        for (Object object : this.controllers) {
            final ClassControllerHolder holder = new ClassControllerHolder(object, pluginHolder, checkerMap);
            for (Entry<Integer, MethodControllerHolder> entry : holder.getMethodHolderMap().entrySet()) {
                MethodControllerHolder controller = entry.getValue();
                Map<MessageMode, MethodControllerHolder> holderMap = methodHolder.computeIfAbsent(controller.getID(), HashMap::new);
                for (MessageMode mode : controller.getMessageModes()) {
                    MethodControllerHolder old = holderMap.putIfAbsent(mode, controller);
                    if (old != null)
                        throw new IllegalArgumentException(LogUtils.format("{} 与 {} 对MessageMode {} 处理发生冲突", old, controller, mode));
                }
            }
        }
        this.methodHolder = new HashMap<>();
        methodHolder.forEach((k, v) -> this.methodHolder.put(k, ImmutableMap.copyOf(v)));
        this.methodHolder = ImmutableMap.copyOf(this.methodHolder);
    }

}

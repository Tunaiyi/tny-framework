package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.dispatcher.plugin.PluginHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

public class DefaultMessageDispatcher extends NetMessageDispatcher {

    private List<Object> controllers = new ArrayList<>();

    public DefaultMessageDispatcher(boolean checkTimeOut) {
        super(checkTimeOut);
    }

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
        NetSessionHolder sessionHolder = appContext.getSessionHolder();
        if (sessionHolder == null)
            throw new NullPointerException("sessionHolder is null");
        for (Object object : this.controllers) {
            final ClassControllerHolder holder = new ClassControllerHolder(object, pluginHolder);
            for (Entry<Integer, MethodControllerHolder> entry : holder.getMethodHolderMap().entrySet()) {
                MethodControllerHolder methodHolder = entry.getValue();
                MethodControllerHolder old = this.methodHolder.put(methodHolder.getID(), methodHolder);
                if (old != null)
                    throw new IllegalArgumentException(LogUtils.format("{} 与 {} name 发生冲突", old, methodHolder));
            }
        }
    }

}

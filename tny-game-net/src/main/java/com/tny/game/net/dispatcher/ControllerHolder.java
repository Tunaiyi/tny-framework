package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.annotation.Controller;
import com.tny.game.annotation.Plugin;
import com.tny.game.common.reflect.GClass;
import com.tny.game.common.reflect.GMethod;
import com.tny.game.common.reflect.MethodFilter;
import com.tny.game.common.reflect.javassist.JSsistUtils;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ControllerHolder extends AbstractControllerHolder {

    private static final List<Method> OBJECT_METHOD_LIST = Arrays.asList(Object.class.getMethods());

    /**
     * 执行后插件
     */
    private final Map<Integer, MethodHolder> methodHolderMap;

    public ControllerHolder(final Object executor, final PluginHolder pluginHolder) {
        super(pluginHolder, executor.getClass().getAnnotation(Controller.class), executor.getClass().getAnnotation(Plugin.class), executor);
        if (this.controller == null)
            throw new IllegalArgumentException(this.clazz + " is not Controller Object");
        this.initMethodAnnotation(executor.getClass().getAnnotations());
        this.methodHolderMap = new CopyOnWriteMap<>();
        this.initMethodHolder(executor);
    }

    private static final MethodFilter FILTER = new MethodFilter() {

        @Override
        public boolean filter(Method method) {
            if (OBJECT_METHOD_LIST.indexOf(method) > -1)
                return true;
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
                return false;
            //			Class<?>[] parameterClass = method.getParameterTypes();
            //			if (parameterClass.length <= 0)
            //				return true;
            //			if (!Request.class.isAssignableFrom(parameterClass[0]) &&
            //					parameterClass[0] != int.class && parameterClass[0] != Integer.class)
            //				return true;
            return true;
        }

    };

    protected void initMethodHolder(final Object executor) {
        GClass access = JSsistUtils.getGClass(executor.getClass(), FILTER);
        for (GMethod method : access.getGMethodList()) {
            Controller controller = method.getJavaMethod().getAnnotation(Controller.class);
            if (controller == null)
                continue;
            MethodHolder holder = new MethodHolder(executor, this, method, controller, this.pluginHolder);
            if (holder.getID() > 0) {
                MethodHolder last = this.methodHolderMap.put(holder.getID(), holder);
                if (last != null)
                    throw new IllegalArgumentException(LogUtils.format("{} controller 中的 {} 与 {} 的 ID:{} 发生冲突", this.getName(), last.getName(), holder.getName(), holder.getID()));
            }
        }
    }

    public Map<Integer, MethodHolder> getMethodHolderMap() {
        return Collections.unmodifiableMap(methodHolderMap);
    }

    protected boolean filterMethod(Method method) {
        if (OBJECT_METHOD_LIST.indexOf(method) > -1)
            return true;
        Class<?>[] parameterClass = method.getParameterTypes();
        if (parameterClass.length <= 0)
            return true;
        if (!Request.class.isAssignableFrom(parameterClass[0]) && parameterClass[0] != int.class && parameterClass[0] != Integer.class)
            return true;
        return false;
    }

    @Override
    public String getName() {
        final String controlName = this.controller.name();
        if (controlName == null || controlName.equals("")) {
            final String className = this.clazz.getName();
            final String[] paths = className.split("\\.");
            return paths[paths.length - 1];
        }
        return controlName;
    }

    @Override
    public int getID() {
        return this.controller.value();
    }

    @Override
    public boolean isCheck() {
        return this.controller.check();
    }

    @Override
    public boolean isAuth() {
        return this.controller.auth();
    }

    // public UserType getUserType() {
    // return this.controller.userType();
    // }

    public long getRequestLife() {
        return this.controller.requestLife();
    }

    public boolean isTimeOut() {
        return this.controller.timeOut();
    }

    @Override
    public List<ControllerPlugin> getControllerPluginBeforeList() {
        return Collections.unmodifiableList(this.pluginBeforeList);
    }

    @Override
    public List<ControllerPlugin> getControllerPluginAfterList() {
        return Collections.unmodifiableList(this.pluginAfterList);
    }

    @Override
    public boolean isUserGroup(String group) {
        return this.userGroupList.indexOf(group) > -1;
    }

    public MethodHolder getMethodHolder(int protocol) {
        return this.methodHolderMap.get(protocol);
    }

    public boolean isCanCall(String serverType) {
        return this.serverTypeList.isEmpty() || this.serverTypeList.indexOf(serverType) > -1;
    }

    @Override
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        return this.getAnnotation0(annotationClass);
    }

}

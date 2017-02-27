package com.tny.game.net.dispatcher;

import com.tny.game.annotation.Controller;
import com.tny.game.annotation.Plugin;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class ControllerHolder {

    private static final Logger LOG = LoggerFactory.getLogger(CoreLogger.DISPATCHER);

    /**
     * 控制器类型
     */
    protected final Class<?> clazz;
    /**
     * 插件管理器
     */
    protected final PluginHolder pluginHolder;
    /**
     * 控制器操作配置
     */
    protected final Controller controller;
    /**
     * 执行前插件
     */
    protected final List<ControllerPlugin> pluginBeforeList;
    /**
     * 执行后插件
     */
    protected final List<ControllerPlugin> pluginAfterList;
    /**
     * 用户组名称列表
     */
    protected final List<String> userGroupList;
    /**
     * 服务器类型列表
     */
    protected final List<String> serverTypeList;
    /**
     * 插件註解
     */
    protected final Plugin plugin;
    /**
     * 方法上的注解
     */
    protected Map<Class<? extends Annotation>, Annotation> methodAnnotation;

    protected ControllerHolder(final PluginHolder pluginHolder, final Controller controller, final Plugin plugin, final Object executor) {
        if (executor == null)
            throw new IllegalArgumentException("executor is null");
        this.clazz = executor.getClass();
        ExceptionUtils.checkNotNull(controller, "{} controller is null", this.clazz);
        this.controller = controller;
        ExceptionUtils.checkNotNull(pluginHolder, "{} pluginHolder is null", this.clazz);
        this.pluginHolder = pluginHolder;
        this.userGroupList = new ArrayList<>();
        this.serverTypeList = new ArrayList<>();
        this.pluginBeforeList = new ArrayList<>();
        this.pluginAfterList = new ArrayList<>();
        this.userGroupList.addAll(Arrays.asList(this.controller.userGroup()));
        this.serverTypeList.addAll(Arrays.asList(this.controller.appType()));
        this.plugin = plugin;
        if (this.plugin != null) {
            this.initPlugin(plugin.before(), this.pluginBeforeList);
            this.initPlugin(plugin.after(), this.pluginAfterList);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends ControllerPlugin> void initPlugin(final Class<? extends ControllerPlugin>[] pluginClasses,
                                                         final List<E> pluginList) {
        for (Class<? extends ControllerPlugin> pluginClass : pluginClasses) {
            if (pluginClass == null)
                continue;
            try {
                final ControllerPlugin plugin = this.pluginHolder.getPlugin(pluginClass);
                if (plugin != null)
                    pluginList.add((E) plugin);
            } catch (Exception e) {
                LOG.warn("#AbstrectControllerHolder#初始化插件# {} 异常", pluginClass, e);
            }
        }
    }

    protected void initMethodAnnotation(Annotation[] annotations) {
        Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
        for (Annotation annotation : annotations)
            annotationMap.put(annotation.getClass(), annotation);
        this.methodAnnotation = Collections.unmodifiableMap(annotationMap);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Annotation> A getAnnotation0(Class<A> annotationClass) {
        return (A) this.methodAnnotation.get(annotationClass);
    }

    public abstract List<ControllerPlugin> getControllerPluginBeforeList();

    public abstract List<ControllerPlugin> getControllerPluginAfterList();

    public abstract boolean isCheck();

    public abstract boolean isAuth();

    public abstract boolean isUserGroup(String group);

    public abstract String getName();

    public abstract int getID();

    public abstract <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass);

}

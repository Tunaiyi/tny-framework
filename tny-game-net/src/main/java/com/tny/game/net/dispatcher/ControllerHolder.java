package com.tny.game.net.dispatcher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tny.game.LogUtils;
import com.tny.game.annotation.Auth;
import com.tny.game.annotation.Check;
import com.tny.game.annotation.Checkers;
import com.tny.game.annotation.Controller;
import com.tny.game.annotation.MessageFilter;
import com.tny.game.annotation.Plugin;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.log.NetLogger;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.plugin.ControllerPlugin;
import com.tny.game.net.plugin.PluginHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ControllerHolder {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    /**
     * 控制器类型
     */
    protected final Class<?> controllerClass;
    /**
     * 插件管理器
     */
    protected final PluginHolder pluginHolder;
    /**
     * 控制器操作配置
     */
    protected final Controller controller;

    /**
     * 是否需要验证授权
     */
    protected final Auth auth;

    /**
     * 消息处理
     */
    protected Set<MessageMode> messageModes;

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
    protected final List<String> userGroups;
    /**
     * 插件註解
     */
    protected final Plugin plugin;

    /**
     * 检测器列表
     */
    protected List<CheckerHolder> checkerHolders;


    protected ControllerHolder(final PluginHolder pluginHolder, final Controller controller, final Plugin plugin, final MessageFilter filter, final Checkers checkers, final Object executor, Map<Class<?>, ControllerChecker> checkerMap) {
        if (executor == null)
            throw new IllegalArgumentException("executor is null");
        this.controllerClass = executor.getClass();
        ExceptionUtils.checkNotNull(controller, "{} controller is null", this.controllerClass);
        this.controller = controller;
        this.auth = this.controllerClass.getAnnotation(Auth.class);
        if (this.auth != null && this.auth.enable())
            this.userGroups = ImmutableList.copyOf(this.auth.value());
        else
            this.userGroups = ImmutableList.of();
        ExceptionUtils.checkNotNull(pluginHolder, "{} pluginHolder is null", this.controllerClass);
        this.pluginHolder = pluginHolder;
        this.pluginBeforeList = new ArrayList<>();
        this.pluginAfterList = new ArrayList<>();
        // this.userGroupList.addAll(Arrays.asList(this.controller.userGroup()));
        // this.serverTypeList.addAll(Arrays.asList(this.controller.appType()));
        this.plugin = plugin;
        if (this.plugin != null) {
            this.initPlugin(plugin.before(), this.pluginBeforeList);
            this.initPlugin(plugin.after(), this.pluginAfterList);
        }

        List<CheckerHolder> checkerHolders = new ArrayList<>();
        if (checkers != null) {
            for (Check check : checkers.value()) {
                ControllerChecker msgChecker = checkerMap.get(check.value());
                if (msgChecker == null)
                    throw new NullPointerException(LogUtils.format("{} class MessageChecker is null", check.value()));
                checkerHolders.add(new CheckerHolder(this, msgChecker, check));
            }
        }
        this.checkerHolders = ImmutableList.copyOf(checkerHolders);

        if (filter != null)
            this.messageModes = ImmutableSet.copyOf(filter.modes());
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

    public boolean isAuth() {
        return this.auth != null && this.auth.enable();
    }

    protected List<CheckerHolder> getCheckerHolders() {
        return checkerHolders;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    protected boolean isUserGroup(String group) {
        if (this.userGroups.isEmpty())
            return true;
        return this.userGroups.contains(group);
    }

    protected abstract List<ControllerPlugin> getControllerPluginBeforeList();

    protected abstract List<ControllerPlugin> getControllerPluginAfterList();

    public abstract String getName();

    public abstract int getID();

    public abstract <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    public abstract <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass);
    /**
     * 获取某方上参数指定注解类型的注解列表
     *
     * @param clazz 指定的注解类型
     * @return 注解列表 ( @A int a, int b, @A int c, @B int d) <br/>
     * 获取 @A : [@A, null, @A, null] <br/>
     * 获取 @B : [null, null, null, @B]
     */
    public abstract <A extends Annotation> List<A> getParamsAnnotationsByType(Class<A> clazz);


    /**
     * 获取某个参数上的注解列表
     *
     * @param index 参数位置索引
     * @return 返回指定参数的注解列表 ( @A @B int a, int b, @A int C) 获取 0 : [@A, @B] 获取 1
     * : [] 获取 2 : [@A]
     */
    public abstract List<Annotation> getParamAnnotationsByIndex(int index);

    public Set<MessageMode> getMessageModes() {
        return messageModes;
    }

    public abstract boolean isParamsAnnotationExist(Class<? extends Annotation> clazz);

}

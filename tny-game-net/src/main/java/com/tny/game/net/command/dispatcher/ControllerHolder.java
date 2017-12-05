package com.tny.game.net.command.dispatcher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tny.game.common.utils.Throws;
import com.tny.game.net.annotation.AfterPlugin;
import com.tny.game.net.annotation.AppProfile;
import com.tny.game.net.annotation.Auth;
import com.tny.game.net.annotation.BeforePlugin;
import com.tny.game.net.annotation.Check;
import com.tny.game.net.annotation.Controller;
import com.tny.game.net.annotation.MessageFilter;
import com.tny.game.net.annotation.ScopeProfile;
import com.tny.game.net.command.auth.AuthProvider;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.checker.ControllerChecker;
import com.tny.game.net.common.ControllerCheckerHolder;
import com.tny.game.net.message.MessageMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ControllerHolder {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    /**
     * 控制器类型
     */
    protected final Class<?> controllerClass;
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
    protected List<ControllerPlugin> beforePlugins;
    /**
     * 执行后插件
     */
    protected List<ControllerPlugin> afterPlugins;
    /**
     * 用户组名称列表
     */
    protected final List<String> userGroups;

    /**
     * 应用类型
     */
    protected final List<String> appTypes;

    /**
     * 应用类型
     */
    protected final List<String> scopes;

    /**
     * 检测器列表
     */
    protected List<ControllerCheckerHolder> checkerHolders;

    protected ControllerHolder(final Object executor, final AbstractMessageDispatcher dispatcher, final Controller controller, final BeforePlugin[] beforePlugins, final AfterPlugin[] afterPlugins, final Auth auth, final Check[] checkers, final MessageFilter filter, final AppProfile appProfile, final ScopeProfile scopeProfile) {
        if (executor == null)
            throw new IllegalArgumentException("executor is null");
        this.controllerClass = executor.getClass();
        Throws.checkNotNull(controller, "{} controller is null", this.controllerClass);
        this.controller = controller;
        this.auth = auth;
        if (this.auth != null && this.auth.enable())
            this.userGroups = ImmutableList.copyOf(this.auth.value());
        else
            this.userGroups = null;
        if (appProfile != null)
            this.appTypes = ImmutableList.copyOf(appProfile.value());
        else
            this.appTypes = null;
        if (scopeProfile != null)
            this.scopes = ImmutableList.copyOf(scopeProfile.value());
        else
            this.scopes = null;
        if (beforePlugins != null)
            this.initPlugins(dispatcher, Stream.of(beforePlugins)
                    .map(BeforePlugin::value)
                    .collect(Collectors.toList()), this.beforePlugins = new ArrayList<>());
        if (afterPlugins != null)
            this.initPlugins(dispatcher, Stream.of(afterPlugins)
                    .map(AfterPlugin::value)
                    .collect(Collectors.toList()), this.afterPlugins = new ArrayList<>());

        if (checkers != null)
            this.initChecker(dispatcher, checkers);

        if (filter != null)
            this.messageModes = ImmutableSet.copyOf(filter.modes());
    }

    @SuppressWarnings("unchecked")
    private <E extends ControllerPlugin> void initChecker(AbstractMessageDispatcher dispatcher, final Check[] checkers) {
        List<ControllerCheckerHolder> checkerHolders = new ArrayList<>();
        for (Check check : checkers) {
            ControllerChecker checker = dispatcher.getChecker(check.value());
            Throws.checkNotNull(checker, "{} Checker is null", check.value());
            checkerHolders.add(new ControllerCheckerHolder(this, checker, check));
        }
        this.checkerHolders = ImmutableList.copyOf(checkerHolders);
    }


    @SuppressWarnings("unchecked")
    private <E extends ControllerPlugin> void initPlugins(AbstractMessageDispatcher dispatcher,
                                                          final Collection<Class<? extends ControllerPlugin>> pluginClasses,
                                                          final List<E> pluginList) {
        for (Class<? extends ControllerPlugin> pluginClass : pluginClasses) {
            if (pluginClass == null)
                continue;
            final ControllerPlugin plugin = dispatcher.getPlugin(pluginClass);
            Throws.checkNotNull(plugin, "{} plugin is null", pluginClass);
            pluginList.add((E) plugin);
        }
    }

    public boolean isAuth() {
        return this.auth != null && this.auth.enable();
    }

    public Class<?> getAuthProvider() {
        if (this.auth != null && this.auth.enable() && this.auth.provider() != AuthProvider.class)
            return this.auth.provider();
        return null;
    }

    public List<ControllerCheckerHolder> getCheckerHolders() {
        return checkerHolders;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public boolean isUserGroup(String group) {
        return this.userGroups == null || this.userGroups.isEmpty() || this.userGroups.contains(group);
    }

    public boolean isActiveByAppType(String appType) {
        return this.appTypes == null || this.appTypes.isEmpty() || this.appTypes.contains(appType);
    }

    public boolean isActiveByScope(String scope) {
        return this.scopes == null || this.scopes.isEmpty() || this.scopes.contains(scope);
    }

    protected abstract List<ControllerPlugin> getControllerBeforePlugins();

    protected abstract List<ControllerPlugin> getControllerAfterPlugins();

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

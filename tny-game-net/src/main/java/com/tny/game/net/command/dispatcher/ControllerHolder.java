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
package com.tny.game.net.command.dispatcher;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.plugins.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class ControllerHolder {

    /**
     * 控制器类型
     */
    protected final Class<?> controllerClass;

    /**
     * 是否需要验证授权
     */
    protected final AuthenticationRequired auth;

    /**
     * 执行前插件
     */
    protected List<CommandPluginHolder> beforePlugins;

    /**
     * 执行后插件
     */
    protected List<CommandPluginHolder> afterPlugins;

    /**
     * 用户组名称列表
     */
    protected final List<String> contactGroups;

    /**
     * 应用类型
     */
    protected final List<String> appTypes;

    /**
     * 应用类型
     */
    protected final List<String> scopes;

    protected ControllerHolder(final Object executor, final MessageDispatcherContext context,
            final BeforePlugin[] beforePlugins, final AfterPlugin[] afterPlugins, final AuthenticationRequired auth,
            final AppProfile appProfile, final ScopeProfile scopeProfile, ExprHolderFactory exprHolderFactory) {
        if (executor == null) {
            throw new IllegalArgumentException("executor is null");
        }
        this.controllerClass = executor.getClass();
        this.auth = auth;
        if (this.auth != null && this.auth.enable()) {
            this.contactGroups = ImmutableList.copyOf(this.auth.value());
        } else {
            this.contactGroups = null;
        }
        if (appProfile != null) {
            this.appTypes = ImmutableList.copyOf(appProfile.value());
        } else {
            this.appTypes = null;
        }
        if (scopeProfile != null) {
            this.scopes = ImmutableList.copyOf(scopeProfile.value());
        } else {
            this.scopes = null;
        }
        if (beforePlugins != null) {
            this.beforePlugins = this
                    .initPlugins(context, Arrays.asList(beforePlugins), exprHolderFactory, BeforePlugin::value, CommandPluginHolder::new);
        }
        if (afterPlugins != null) {
            this.afterPlugins = this
                    .initPlugins(context, Arrays.asList(afterPlugins), exprHolderFactory, AfterPlugin::value, CommandPluginHolder::new);
        }
    }

    @SuppressWarnings({"rawtypes"})
    private <A extends Annotation> ImmutableList<CommandPluginHolder> initPlugins(MessageDispatcherContext context,
            final Collection<? extends A> pluginAnnotations,
            ExprHolderFactory exprHolderFactory,
            Function<A, Class<? extends CommandPlugin>> pluginClassGetter,
            ControllerPluginHolderConstructor<A> holderFactory,
            CommandPluginHolder... defaultHolders) {
        List<CommandPluginHolder> plugins = new ArrayList<>(Arrays.asList(defaultHolders));
        for (A pluginAnnotation : pluginAnnotations) {
            Class<? extends CommandPlugin> pluginClass = pluginClassGetter.apply(pluginAnnotation);
            final CommandPlugin<?, ?> plugin = context.getPlugin(as(pluginClass));
            Asserts.checkNotNull(plugin, "{} plugin is null", pluginClass);
            plugins.add(holderFactory.create(this, plugin, pluginAnnotation, exprHolderFactory));
        }
        return ImmutableList.copyOf(plugins);
    }

    private interface ControllerPluginHolderConstructor<T> {

        CommandPluginHolder create(ControllerHolder controller, CommandPlugin<?, ?> plugin, T annotation, ExprHolderFactory exprHolderFactory);

    }

    public boolean isAuth() {
        return this.auth != null && this.auth.enable();
    }

    public Class<? extends AuthenticationValidator<?, ?>> getAuthValidator() {
        if (this.auth != null && this.auth.enable() && this.auth.validator() != AuthenticationValidator.class) {
            return as(this.auth.validator());
        }
        return null;
    }

    public Class<?> getControllerClass() {
        return this.controllerClass;
    }

    public boolean isContactGroup(ContactType contactType) {
        return this.contactGroups == null || this.contactGroups.isEmpty() || this.contactGroups.contains(contactType.getGroup());
    }

    public boolean isActiveByAppType(String appType) {
        return this.appTypes == null || this.appTypes.isEmpty() || this.appTypes.contains(appType);
    }

    public boolean isActiveByScope(String scope) {
        return this.scopes == null || this.scopes.isEmpty() || this.scopes.contains(scope);
    }

    protected abstract List<CommandPluginHolder> getControllerBeforePlugins();

    protected abstract List<CommandPluginHolder> getControllerAfterPlugins();

    public abstract String getName();

    public abstract <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    public abstract <A extends Annotation> List<A> getAnnotations(Class<A> annotationClass);

}

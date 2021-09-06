package com.tny.game.net.command.dispatcher;

import com.google.common.collect.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.message.*;

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
	 * 控制器操作配置
	 */
	protected final Controller controller;

	/**
	 * 是否需要验证授权
	 */
	protected final AuthenticationRequired auth;

	/**
	 * 消息处理
	 */
	protected Set<MessageMode> messageModes;

	/**
	 * 执行前插件
	 */
	protected List<ControllerPluginHolder> beforePlugins;

	/**
	 * 执行后插件
	 */
	protected List<ControllerPluginHolder> afterPlugins;

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

	protected ControllerHolder(final Object executor, final MessageDispatcherContext context, final Controller controller,
			final BeforePlugin[] beforePlugins, final AfterPlugin[] afterPlugins, final AuthenticationRequired auth, final MessageFilter filter,
			final AppProfile appProfile, final ScopeProfile scopeProfile, ExprHolderFactory exprHolderFactory) {
		if (executor == null) {
			throw new IllegalArgumentException("executor is null");
		}
		this.controllerClass = executor.getClass();
		Asserts.checkNotNull(controller, "{} controller is null", this.controllerClass);
		this.controller = controller;
		this.auth = auth;
		if (this.auth != null && this.auth.enable()) {
			this.userGroups = ImmutableList.copyOf(this.auth.value());
		} else {
			this.userGroups = null;
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
					.initPlugins(context, Arrays.asList(beforePlugins), exprHolderFactory, BeforePlugin::value, ControllerPluginHolder::new);
		}
		if (afterPlugins != null) {
			this.afterPlugins = this
					.initPlugins(context, Arrays.asList(afterPlugins), exprHolderFactory, AfterPlugin::value, ControllerPluginHolder::new);
		}

		if (filter != null) {
			this.messageModes = ImmutableSet.copyOf(filter.modes());
		}
	}

	@SuppressWarnings({"rawtypes"})
	private <A extends Annotation> ImmutableList<ControllerPluginHolder> initPlugins(MessageDispatcherContext context,
			final Collection<? extends A> pluginAnnotations,
			ExprHolderFactory exprHolderFactory,
			Function<A, Class<? extends CommandPlugin>> pluginClassGetter,
			ControllerPluginHolderConstructor<A> holderFactory,
			ControllerPluginHolder... defaultHolders) {
		List<ControllerPluginHolder> plugins = new ArrayList<>(Arrays.asList(defaultHolders));
		for (A pluginAnnotation : pluginAnnotations) {
			Class<? extends CommandPlugin> pluginClass = pluginClassGetter.apply(pluginAnnotation);
			final CommandPlugin<?, ?> plugin = context.getPlugin(as(pluginClass));
			Asserts.checkNotNull(plugin, "{} plugin is null", pluginClass);
			plugins.add(holderFactory.create(this, plugin, pluginAnnotation, exprHolderFactory));
		}
		return ImmutableList.copyOf(plugins);
	}

	private interface ControllerPluginHolderConstructor<T> {

		ControllerPluginHolder create(ControllerHolder controller, CommandPlugin<?, ?> plugin, T annotation, ExprHolderFactory exprHolderFactory);

	}

	public boolean isAuth() {
		return this.auth != null && this.auth.enable();
	}

	public Class<? extends AuthenticateValidator<?>> getAuthValidator() {
		if (this.auth != null && this.auth.enable() && this.auth.validator() != AuthenticateValidator.class) {
			return as(this.auth.validator());
		}
		return null;
	}

	public Class<?> getControllerClass() {
		return this.controllerClass;
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

	protected abstract List<ControllerPluginHolder> getControllerBeforePlugins();

	protected abstract List<ControllerPluginHolder> getControllerAfterPlugins();

	public abstract String getName();

	public abstract int getId();

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
		return this.messageModes;
	}

	public abstract boolean isParamsAnnotationExist(Class<? extends Annotation> clazz);

}

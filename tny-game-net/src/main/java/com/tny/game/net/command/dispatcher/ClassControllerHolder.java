package com.tny.game.net.command.dispatcher;

import com.google.common.collect.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.javassist.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public final class ClassControllerHolder extends ControllerHolder {

	private static final List<Method> OBJECT_METHOD_LIST = Arrays.asList(Object.class.getMethods());

	/**
	 * Class的注解
	 */
	private Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();

	private List<MethodControllerHolder> controllers = new ArrayList<>();

	public ClassControllerHolder(final Object executor, final MessageDispatcherContext context, ExprHolderFactory exprHolderFactory) {
		super(executor, context,
				executor.getClass().getAnnotationsByType(BeforePlugin.class),
				executor.getClass().getAnnotationsByType(AfterPlugin.class),
				executor.getClass().getAnnotation(AuthenticationRequired.class),
				executor.getClass().getAnnotation(AppProfile.class),
				executor.getClass().getAnnotation(ScopeProfile.class),
				exprHolderFactory);
		RpcController controller = executor.getClass().getAnnotation(RpcController.class);
		Asserts.checkNotNull(controller, "{} controller is null", this.controllerClass);
		for (Annotation annotation : this.controllerClass.getAnnotations())
			this.annotationMap.put(annotation.getClass(), annotation);
		Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
		this.annotationMap = ImmutableMap.copyOf(annotationMap);
		this.initMethodHolder(executor, context, exprHolderFactory);
		this.setMessageModes(controller.modes());
	}

	private static final MethodFilter FILTER = method -> OBJECT_METHOD_LIST.contains(method) ||
			!(Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()));

	private void initMethodHolder(final Object executor, final MessageDispatcherContext context, ExprHolderFactory exprHolderFactory) {
		ClassAccessor access = JavassistAccessors.getGClass(executor.getClass(), FILTER);
		for (MethodAccessor method : access.getGMethodList()) {
			RpcProfile rpcProfile = RpcProfile.of(method.getJavaMethod());
			if (rpcProfile == null) {
				continue;
			}
			MethodControllerHolder holder = new MethodControllerHolder(executor, context, exprHolderFactory, this, method, rpcProfile);
			if (holder.getProtocol() > 0) {
				controllers.add(holder);
				//				MethodControllerHolder last = this.methodHolderMap.put(holder.getProtocol(), holder);
				//				if (last != null) {
				//					throw new IllegalArgumentException(
				//							format("{} controller 中的 {} 与 {} 的 ID:{} 发生冲突", this.getName(), last.getName(), holder.getName(), holder
				//							.getProtocol()));
				//				}
			}
		}
	}

	public List<MethodControllerHolder> getControllers() {
		return Collections.unmodifiableList(controllers);
	}

	// protected boolean filterMethod(Method method) {
	//     if (OBJECT_METHOD_LIST.indexOf(method) > -1)
	//         return true;
	//     Class<?>[] parameterClass = method.getParameterTypes();
	//     return parameterClass.length <= 0 || !Request.class.isAssignableFrom(parameterClass[0]) &&
	//             parameterClass[0] != int.class && parameterClass[0] != Integer.class;
	// }

	@Override
	public String getName() {
		return this.controllerClass.getCanonicalName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		return (A)this.annotationMap.get(annotationClass);
	}

	@Override
	public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
		return null;
	}

	@Override
	public <A extends Annotation> List<A> getParamsAnnotationsByType(Class<A> clazz) {
		return null;
	}

	@Override
	public List<Annotation> getParamAnnotationsByIndex(int index) {
		return null;
	}

	@Override
	public boolean isParamsAnnotationExist(Class<? extends Annotation> clazz) {
		return false;
	}

	// @Override
	// public boolean isCheck() {
	//     return this.controller.check();
	// }

	// public long getTimeout() {
	//     return this.controller.timeOut();
	// }

	// public boolean isTimeOut() {
	//     return this.controller.timeOut() > 0;
	// }

	@Override
	protected List<ControllerPluginHolder> getControllerBeforePlugins() {
		if (this.beforePlugins == null) {
			return ImmutableList.of();
		}
		return Collections.unmodifiableList(this.beforePlugins);
	}

	@Override
	protected List<ControllerPluginHolder> getControllerAfterPlugins() {
		if (this.afterPlugins == null) {
			return ImmutableList.of();
		}
		return Collections.unmodifiableList(this.afterPlugins);
	}

	// @Override
	// public boolean isUserGroup(String group) {
	//     return this.userGroupList.indexOf(group) > -1;
	// }

	// public MethodControllerHolder getMethodHolder(int protocol) {
	//     return this.methodHolderMap.get(protocol);
	// }

	// public boolean isCanCall(String serverType) {
	//     return this.serverTypeList.isEmpty() || this.serverTypeList.indexOf(serverType) > -1;
	// }

	// @Override
	// public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
	//     return this.getAnnotation0(annotationClass);
	// }

}

package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableSet;
import com.tny.game.net.command.*;
import com.tny.game.net.rpc.annotation.*;
import com.tny.game.net.transport.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.tny.game.net.rpc.RpcMode.*;
import static com.tny.game.net.rpc.annotation.RpcInvocation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/27 5:10 下午
 */
public enum RpcReturnMode {

	/**
	 * Future 对象
	 */
	FUTURE(ImmutableSet.of(REQUEST),
			ImmutableSet.of(MessageRespondAwaiter.class, Future.class, CompletionStage.class, CompletableFuture.class, RpcFuture.class),
			RpcReturnMode::genericBodyClass,
			// INFO 调用方式
			/*默认*/ASYNC),

	/**
	 * Future 对象
	 */
	RESULT(ImmutableSet.of(REQUEST),
			ImmutableSet.of(RpcResult.class),
			(m) -> RpcResult.class,
			// INFO 调用方式
			/*默认*/SYNC),
	/**
	 * void 对象
	 */
	VOID(ImmutableSet.of(REQUEST, PUSH),
			ImmutableSet.of(Void.class, void.class),
			Method::getReturnType,
			// INFO 调用方式
			/*默认*/ASYNC, /*可选*/SYNC),

	/**
	 * 对象
	 */
	OBJECT(
			ImmutableSet.of(REQUEST),
			ImmutableSet.of(),
			Method::getReturnType,
			// INFO 调用方式
			/*默认*/SYNC),

	//
	;

	private final RpcInvocation defaultInvocation;

	private final Set<RpcInvocation> invocations;

	private final Set<RpcMode> modes;

	private final Set<Class<?>> returnClasses;

	private final Function<Method, Class<?>> bodyTypeFinder;

	/**
	 * @param modes             发送类型列表
	 * @param returnClasses     返回值类型
	 * @param defaultInvocation 默认调用方式
	 * @param invocations       额外调用方式
	 */
	RpcReturnMode(Set<RpcMode> modes, Set<Class<?>> returnClasses, Function<Method, Class<?>> bodyTypeGetter, RpcInvocation defaultInvocation,
			RpcInvocation... invocations) {
		this.defaultInvocation = defaultInvocation;
		this.bodyTypeFinder = bodyTypeGetter;
		this.invocations = ImmutableSet.<RpcInvocation>builder()
				.add(defaultInvocation)
				.addAll(Arrays.asList(invocations))
				.build();
		this.modes = ImmutableSet.copyOf(modes);
		this.returnClasses = ImmutableSet.copyOf(returnClasses);
	}

	public static RpcReturnMode typeOf(Class<?> returnClass) {
		for (RpcReturnMode type : RpcReturnMode.values()) {
			if (type.isCanReturn(returnClass)) {
				return type;
			}
		}
		return OBJECT;
	}

	public RpcInvocation checkInvocation(RpcInvocation invocation) {
		if (invocation == DEFAULT) {
			return defaultInvocation;
		}
		return invocation;
	}

	public RpcInvocation getDefaultInvocation() {
		return defaultInvocation;
	}

	public boolean isAsync() {
		return invocations.contains(ASYNC);
	}

	public boolean isCanInvokeBy(RpcMode mode) {
		return modes.contains(mode);
	}

	public boolean isCanReturn(Class<?> value) {
		return returnClasses.contains(value);
	}

	Class<?> findBodyClass(Method method) {
		return this.bodyTypeFinder.apply(method);
	}

	private static Class<?> genericBodyClass(Method method) {
		Type type = method.getGenericReturnType();
		if (type instanceof Class) {
			return (Class<?>)type;
		}
		if (type instanceof ParameterizedType) {
			Type[] actualTypeValue = ((ParameterizedType)type).getActualTypeArguments();
			Type typeClass = actualTypeValue[0];
			if (typeClass instanceof ParameterizedType) {
				ParameterizedType bodyType = (ParameterizedType)typeClass;
				return (Class<?>)bodyType.getRawType();
			}
			return (Class<?>)typeClass;
		}
		throw new IllegalArgumentException();
	}

}

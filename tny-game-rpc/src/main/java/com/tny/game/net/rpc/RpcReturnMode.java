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
package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableSet;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.annotation.*;
import com.tny.game.net.transport.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.tny.game.net.message.MessageMode.*;
import static com.tny.game.net.rpc.annotation.RpcInvokeMode.*;

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
            ImmutableSet.of(MessageRespondFuture.class, Future.class, CompletionStage.class, CompletableFuture.class, RpcFuture.class),
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

    private final RpcInvokeMode defaultInvocation;

    private final Set<RpcInvokeMode> invocations;

    private final Set<MessageMode> modes;

    private final Set<Class<?>> returnClasses;

    private final Function<Method, Class<?>> bodyTypeFinder;

    /**
     * @param modes             发送类型列表
     * @param returnClasses     返回值类型
     * @param defaultInvocation 默认调用方式
     * @param invocations       额外调用方式
     */
    RpcReturnMode(Set<MessageMode> modes, Set<Class<?>> returnClasses, Function<Method, Class<?>> bodyTypeGetter, RpcInvokeMode defaultInvocation,
            RpcInvokeMode... invocations) {
        this.defaultInvocation = defaultInvocation;
        this.bodyTypeFinder = bodyTypeGetter;
        this.invocations = ImmutableSet.<RpcInvokeMode>builder()
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

    public RpcInvokeMode checkInvocation(RpcInvokeMode invocation) {
        if (invocation == DEFAULT) {
            return defaultInvocation;
        }
        return invocation;
    }

    public RpcInvokeMode getDefaultInvocation() {
        return defaultInvocation;
    }

    public boolean isAsync() {
        return invocations.contains(ASYNC);
    }

    public boolean isCanInvokeBy(MessageMode mode) {
        return modes.contains(mode);
    }

    private boolean isCanReturn(Class<?> value) {
        return returnClasses.contains(value);
    }

    Class<?> findBodyClass(Method method) {
        return this.bodyTypeFinder.apply(method);
    }

    private static Class<?> genericBodyClass(Method method) {
        Type type = method.getGenericReturnType();
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type[] actualTypeValue = ((ParameterizedType) type).getActualTypeArguments();
            Type typeClass = actualTypeValue[0];
            if (typeClass instanceof ParameterizedType) {
                ParameterizedType bodyType = (ParameterizedType) typeClass;
                return (Class<?>) bodyType.getRawType();
            }
            return (Class<?>) typeClass;
        }
        throw new IllegalArgumentException();
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.command.dispatcher;

import com.google.common.collect.ImmutableList;
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
    private final AnnotationHolder annotationHolder;

    private final List<MethodControllerHolder> methodControllers;

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
        this.annotationHolder = new AnnotationHolder(this.controllerClass.getAnnotations());
        this.methodControllers = this.initMethodHolder(executor, context, controller, exprHolderFactory);
    }

    private static final MethodFilter FILTER = method -> !OBJECT_METHOD_LIST.contains(method) &&
                                                         Modifier.isPublic(method.getModifiers()) &&
                                                         !Modifier.isStatic(method.getModifiers());

    private List<MethodControllerHolder> initMethodHolder(final Object executor, final MessageDispatcherContext context,
            RpcController controller, ExprHolderFactory exprHolderFactory) {
        List<MethodControllerHolder> methodControllers = new ArrayList<>();
        ClassAccessor access = JavassistAccessors.getGClass(executor.getClass(), FILTER);
        for (MethodAccessor method : access.getGMethodList()) {
            Method javaMethod = method.getJavaMethod();
            if (javaMethod.isBridge()) {
                continue;
            }
            List<RpcProfile> rpcProfiles = RpcProfile.allOf(method.getJavaMethod(), controller.modes());
            if (rpcProfiles.isEmpty()) {
                continue;
            }

            for (RpcProfile profile : rpcProfiles) {
                MethodControllerHolder holder = new MethodControllerHolder(executor, context, exprHolderFactory, this, method, profile);
                if (holder.getProtocol() > 0) {
                    methodControllers.add(holder);
                }
            }
        }
        return ImmutableList.copyOf(methodControllers);
    }

    public List<MethodControllerHolder> getMethodControllers() {
        return methodControllers;
    }

    @Override
    public String getName() {
        return this.controllerClass.getCanonicalName();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return annotationHolder.getAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> List<A> getAnnotations(Class<A> annotationClass) {
        return annotationHolder.getAnnotations(annotationClass);
    }

    @Override
    protected List<CommandPluginHolder> getControllerBeforePlugins() {
        if (this.beforePlugins == null) {
            return ImmutableList.of();
        }
        return Collections.unmodifiableList(this.beforePlugins);
    }

    @Override
    protected List<CommandPluginHolder> getControllerAfterPlugins() {
        if (this.afterPlugins == null) {
            return ImmutableList.of();
        }
        return Collections.unmodifiableList(this.afterPlugins);
    }

}

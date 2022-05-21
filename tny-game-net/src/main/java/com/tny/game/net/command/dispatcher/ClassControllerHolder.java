package com.tny.game.net.command.dispatcher;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.javassist.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;

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
            if (javaMethod.getReturnType() == RpcReturn.class) {
                continue;
            }
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

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
package com.tny.game.common.reflect.proxy;

import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.aop.*;
import com.tny.game.common.reflect.javassist.*;
import javassist.*;
import org.slf4j.*;

import java.lang.reflect.Modifier;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

public class WrapperProxyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WrapperProxyFactory.class);

    private final static ConcurrentMap<Class<?>, Class<?>> WRAPPER_CLASS_MAP = new ConcurrentHashMap<>();

    private final static String PROXY_CLASS_NAME = ".WrapperProxy$$";

    public static <T> WrapperProxy<T> createWrapper(T proxied)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<WrapperProxy<T>> wrapperClazz = getWrapperProxyClass(proxied.getClass());
        if (wrapperClazz == null) {
            throw new NullPointerException(proxied.getClass() + "WrapperProxyClass is null");
        }
        WrapperProxy<T> wrapperProxy = wrapperClazz.getDeclaredConstructor().newInstance();
        wrapperProxy.set$Proxied(proxied);
        return wrapperProxy;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<WrapperProxy<T>> getWrapperProxyClass(Class<?> targetClass) {
        Class<?> wrapperClass = WRAPPER_CLASS_MAP.get(targetClass);
        if (wrapperClass != null) {
            return (Class<WrapperProxy<T>>) wrapperClass;
        }
        // 整体加锁（原无同步：并发首建双 toClass 同名类 → LinkageError/双产物）
        synchronized (targetClass) {
            wrapperClass = WRAPPER_CLASS_MAP.get(targetClass);
            if (wrapperClass != null) {
                return (Class<WrapperProxy<T>>) wrapperClass;
            }
            return createWrapperProxyClass(targetClass);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<WrapperProxy<T>> createWrapperProxyClass(Class<?> targetClass) {
        ClassPool pool = ClassPool.getDefault();
        try {
            // 匿名/本地/无包名类不可生成（原 getPackage() NPE 被吞、静默返回 null）。
            // JDK9+ 起默认包类的 getPackage() 返回非 null 空名 Package（仅数组/原始类型/primitive 才为 null），
            // 只查 null 会让"无包名"分支永不触发、失败以 ClassFormatError 从代理名拼装处泄漏——空包名同判不可代理。
            Package targetPackage = targetClass.getPackage();
            if (targetPackage == null || targetPackage.getName().isEmpty() || targetClass.isAnonymousClass() || targetClass.isLocalClass()) {
                throw new IllegalStateException("目标类不可代理（匿名/本地/无包名）: " + targetClass.getName());
            }
            String proxyClassName = targetPackage.getName() + PROXY_CLASS_NAME + targetClass.getSimpleName();
            /* 获得DProxy类作为代理类的父类 */
            CtClass proxyClass = pool.makeClass(proxyClassName);
            CtClass superclass = pool.get(targetClass.getName());
            proxyClass.setSuperclass(superclass);
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, proxyClass);
            ctConstructor.setBody("{super();}");
            proxyClass.addConstructor(ctConstructor);
            implementWrapperProxy(pool, targetClass, proxyClass);
            Set<CtMethod> methodSet = new HashSet<>();
            for (Method method : ReflectAide.getDeepMethod(targetClass)) {
                int modifiers = method.getModifiers();
                if (Modifier.isPrivate(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                if (!Modifier.isPublic(modifiers) && method.getDeclaringClass().getPackage() != targetClass.getPackage()) {
                    continue;
                }
                proxyMethod(pool, proxyClass, method, methodSet);
            }
            Class<?> wrapperClass = proxyClass.toClass(targetClass);
            Class<?> old = WRAPPER_CLASS_MAP.putIfAbsent(targetClass, wrapperClass);
            return (Class<WrapperProxy<T>>) (old != null ? old : wrapperClass);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Exception e) {
            // 失败显式化（原返回 null，NPE 在远离成因的 createWrapper 处才爆）
            throw new IllegalStateException("生成 " + targetClass + " 包装代理类失败", e);
        }
    }

    private static CtMethod createCtMethod(ClassPool pool, CtClass cc, Method method) throws NotFoundException {
        Class<?> returnClazz = method.getReturnType();
        CtClass returnCC = returnClazz != null ? pool.get(returnClazz.getCanonicalName()) : null;
        List<CtClass> paramCCs = new ArrayList<>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return new CtMethod(returnCC, method.getName(), paramCCs.toArray(new CtClass[0]), cc);
    }

    private static void implementWrapperProxy(ClassPool pool, Class<?> targetClazz, CtClass cc) throws NotFoundException, CannotCompileException {
        cc.setInterfaces(new CtClass[]{pool.get(WrapperProxy.class.getCanonicalName())});
        CtField beforeAdvice = CtField.make("private " + BeforeAdvice.class.getCanonicalName() + " _wrapper$beforeAdvice;", cc);
        cc.addField(beforeAdvice);
        CtField afterReturningAdvice = CtField
                .make("private " + AfterReturningAdvice.class.getCanonicalName() + " _wrapper$afterReturningAdvice;", cc);
        cc.addField(afterReturningAdvice);
        CtField throwsAdvice = CtField.make("private " + ThrowsAdvice.class.getCanonicalName() + " _wrapper$throwsAdvice;", cc);
        cc.addField(throwsAdvice);
        CtField target = CtField.make("private " + targetClazz.getCanonicalName() + " _wrapper$target;", cc);
        cc.addField(target);
        CtMethod setBeforeAdvice = CtMethod
                .make("public void set$Advice(" + BeforeAdvice.class.getCanonicalName() + " advice) {this._wrapper$beforeAdvice = advice;}", cc);
        cc.addMethod(setBeforeAdvice);
        CtMethod setAfterReturningAdvice = CtMethod.make("public void set$Advice(" + AfterReturningAdvice.class.getCanonicalName() +
                                                         " advice) {this._wrapper$afterReturningAdvice = advice;}", cc);
        cc.addMethod(setAfterReturningAdvice);
        CtMethod setThrowsAdvice = CtMethod
                .make("public void set$Advice(" + ThrowsAdvice.class.getCanonicalName() + " advice) {this._wrapper$throwsAdvice = advice;}", cc);
        cc.addMethod(setThrowsAdvice);
        CtMethod getWrapper = CtMethod.make("public " + Object.class.getCanonicalName() + " get$Wrapper() {return this;}", cc);
        cc.addMethod(getWrapper);
        String setProxiedCode = "public void set$Proxied(Object proxied) {" +
                                "this._wrapper$target = " +
                                InvokerFactory.generateCast("proxied", Object.class, targetClazz) +
                                ";}";
        CtMethod setProxied = CtMethod.make(setProxiedCode, cc);
        cc.addMethod(setProxied);
    }

    private static boolean proxyMethod(ClassPool pool, CtClass cc, Method method, Set<CtMethod> methodSet)
            throws NotFoundException, CannotCompileException {
        Class<?> returnClazz = method.getReturnType();
        int paramSize = method.getParameterTypes().length;
        CtMethod cm = createCtMethod(pool, cc, method);
        if (!methodSet.add(cm)) {
            return false;
        }
        StringBuilder bodyCode = new StringBuilder();
        bodyCode.append("{");
        StringBuilder invokeCode = new StringBuilder();
        invokeCode.append("_wrapper$target.")
                .append(method.getName())
                .append("(");
        for (int paramIndex = 1; paramIndex < paramSize + 1; paramIndex++) {
            invokeCode.append("$").append(paramIndex);
            if (paramIndex != paramSize) {
                invokeCode.append(",");
            }
        }
        invokeCode.append(")");
        if (returnClazz != void.class) {
            bodyCode.append("Object returnValue = ")
                    .append(InvokerFactory.generateCast(invokeCode.toString(), returnClazz, Object.class))
                    .append(";")
                    .append("return ")
                    .append(InvokerFactory.generateCast("returnValue", Object.class, method.getReturnType()));
        } else {
            bodyCode.append(invokeCode.toString());
        }
        bodyCode.append(";}");
        //		bodyCode.append(";}");
        cm.setBody(bodyCode.toString());
        cc.addMethod(cm);
        return true;
    }

}

package com.tny.game.common.reflect.proxy;

import com.tny.game.common.reflect.ReflectUtils;
import com.tny.game.common.reflect.aop.AfterReturningAdvice;
import com.tny.game.common.reflect.aop.BeforeAdvice;
import com.tny.game.common.reflect.aop.ThrowsAdvice;
import com.tny.game.common.reflect.javassist.InvokerFactory;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WrapperProxyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WrapperProxyFactory.class);
    private final static ConcurrentMap<Class<?>, Class<?>> WRAPPER_CLASS_MAP = new ConcurrentHashMap<>();
    private final static String PROXY_CLASS_NAME = ".WrapperProxy$$";

    public static <T> WrapperProxy<T> createWrapper(T proxied) throws InstantiationException, IllegalAccessException {
        Class<WrapperProxy<T>> wrapperClazz = getWrapperProxyClass(proxied.getClass());
        WrapperProxy<T> wrapperProxy = wrapperClazz.newInstance();
        wrapperProxy.set$Proxied(proxied);
        return wrapperProxy;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<WrapperProxy<T>> getWrapperProxyClass(Class<?> targetClass) {
        Class<?> wrapperClass = WRAPPER_CLASS_MAP.get(targetClass);
        if (wrapperClass != null)
            return (Class<WrapperProxy<T>>) wrapperClass;
        ClassPool pool = ClassPool.getDefault();
        try {
            String proxyClassName = targetClass.getPackage().getName() + PROXY_CLASS_NAME + targetClass.getSimpleName();
            /* 获得DProxy类作为代理类的父类 */
            CtClass proxyClass = pool.makeClass(proxyClassName);
            CtClass superclass = pool.get(targetClass.getCanonicalName());
            proxyClass.setSuperclass(superclass);
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, proxyClass);
            ctConstructor.setBody("{super();}");
            proxyClass.addConstructor(ctConstructor);
            implementWrapperProxy(pool, targetClass, proxyClass);

            Set<CtMethod> methodSet = new HashSet<>();
            for (Method method : ReflectUtils.getDeepMethod(targetClass)) {
                int modifiers = method.getModifiers();
                if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers))
                    continue;
                proxyMethod(pool, proxyClass, method, methodSet);
            }
            wrapperClass = proxyClass.toClass();
            Class<?> old = WRAPPER_CLASS_MAP.putIfAbsent(targetClass, wrapperClass);
            return (Class<WrapperProxy<T>>) (old != null ? old : wrapperClass);
        } catch (Exception e) {
            LOGGER.error("生成 {} 代理类错误", targetClass, e);
        }
        return null;
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
        CtField afterReturningAdvice = CtField.make("private " + AfterReturningAdvice.class.getCanonicalName() + " _wrapper$afterReturningAdvice;", cc);
        cc.addField(afterReturningAdvice);
        CtField throwsAdvice = CtField.make("private " + ThrowsAdvice.class.getCanonicalName() + " _wrapper$throwsAdvice;", cc);
        cc.addField(throwsAdvice);
        CtField target = CtField.make("private " + targetClazz.getCanonicalName() + " _wrapper$target;", cc);
        cc.addField(target);

        CtMethod setBeforeAdvice = CtMethod.make("public void set$Advice(" + BeforeAdvice.class.getCanonicalName() + " advice) {this._wrapper$beforeAdvice = advice;}", cc);
        cc.addMethod(setBeforeAdvice);
        CtMethod setAfterReturningAdvice = CtMethod.make("public void set$Advice(" + AfterReturningAdvice.class.getCanonicalName() + " advice) {this._wrapper$afterReturningAdvice = advice;}", cc);
        cc.addMethod(setAfterReturningAdvice);
        CtMethod setThrowsAdvice = CtMethod.make("public void set$Advice(" + ThrowsAdvice.class.getCanonicalName() + " advice) {this._wrapper$throwsAdvice = advice;}", cc);
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

    private static boolean proxyMethod(ClassPool pool, CtClass cc, Method method, Set<CtMethod> methodSet) throws NotFoundException, CannotCompileException {
        Class<?> returnClazz = method.getReturnType();
        int paramSize = method.getParameterTypes().length;
        CtMethod cm = createCtMethod(pool, cc, method);
        if (!methodSet.add(cm))
            return false;
        StringBuilder bodyCode = new StringBuilder();
        bodyCode.append("{");
        StringBuilder invokeCode = new StringBuilder();
        invokeCode.append("_wrapper$target.")
                .append(method.getName())
                .append("(");
        for (int paramIndex = 1; paramIndex < paramSize + 1; paramIndex++) {
            invokeCode.append("$").append(paramIndex);
            if (paramIndex != paramSize)
                invokeCode.append(",");
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

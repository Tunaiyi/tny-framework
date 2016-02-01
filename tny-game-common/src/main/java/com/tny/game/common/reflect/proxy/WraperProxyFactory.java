package com.tny.game.common.reflect.proxy;

import com.tny.game.common.reflect.ReflectUtils;
import com.tny.game.common.reflect.aop.AfterReturningAdvice;
import com.tny.game.common.reflect.aop.BeforeAdvice;
import com.tny.game.common.reflect.aop.ThrowsAdvice;
import com.tny.game.common.reflect.javassist.InvokerFactory;
import javassist.*;
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

public class WraperProxyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WraperProxyFactory.class);
    private final static ConcurrentMap<Class<?>, Class<?>> WRAPER_CLASS_MAP = new ConcurrentHashMap<Class<?>, Class<?>>();
    private final static String PROXY_CLASS_NAME = ".WraperProxy$$";

    public static <T> WrapperProxy<T> createWraper(T proxyed) throws InstantiationException, IllegalAccessException {
        Class<WrapperProxy<T>> wraperClazz = getWraperProxyClass(proxyed.getClass());
        WrapperProxy<T> wrapperProxy = wraperClazz.newInstance();
        wrapperProxy.set$Proxyed(proxyed);
        return wrapperProxy;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<WrapperProxy<T>> getWraperProxyClass(Class<?> targetClass) {
        Class<?> wraperClass = WRAPER_CLASS_MAP.get(targetClass);
        if (wraperClass != null)
            return (Class<WrapperProxy<T>>) wraperClass;
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
            implementWraperProxy(pool, targetClass, proxyClass);

            Set<CtMethod> methodSet = new HashSet<CtMethod>();
            for (Method method : ReflectUtils.getDeepMethod(targetClass)) {
                int modifiers = method.getModifiers();
                if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers))
                    continue;
                proxyMethod(pool, proxyClass, method, methodSet);
            }
            wraperClass = proxyClass.toClass();
            Class<?> old = WRAPER_CLASS_MAP.putIfAbsent(targetClass, wraperClass);
            return (Class<WrapperProxy<T>>) (old != null ? old : wraperClass);
        } catch (Exception e) {
            LOGGER.error("生成 {} 代理类错误", targetClass, e);
        }
        return null;
    }

    private static CtMethod createCtMethod(ClassPool pool, CtClass cc, Method method) throws NotFoundException {
        Class<?> returnClazz = method.getReturnType();
        CtClass returnCC = returnClazz != null ? pool.get(returnClazz.getCanonicalName()) : null;
        List<CtClass> paramCCs = new ArrayList<CtClass>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return new CtMethod(returnCC, method.getName(), paramCCs.toArray(new CtClass[0]), cc);
    }

    private static void implementWraperProxy(ClassPool pool, Class<?> targetClazz, CtClass cc) throws NotFoundException, CannotCompileException {

        cc.setInterfaces(new CtClass[]{pool.get(WrapperProxy.class.getCanonicalName())});

        CtField beforeAdvice = CtField.make("private " + BeforeAdvice.class.getCanonicalName() + " _wraper$beforeAdvice;", cc);
        cc.addField(beforeAdvice);
        CtField afterReturningAdvice = CtField.make("private " + AfterReturningAdvice.class.getCanonicalName() + " _wraper$afterReturningAdvice;", cc);
        cc.addField(afterReturningAdvice);
        CtField throwsAdvice = CtField.make("private " + ThrowsAdvice.class.getCanonicalName() + " _wraper$throwsAdvice;", cc);
        cc.addField(throwsAdvice);
        CtField target = CtField.make("private " + targetClazz.getCanonicalName() + " _wraper$target;", cc);
        cc.addField(target);

        CtMethod setBeforeAdvice = CtMethod.make("public void set$Avice(" + BeforeAdvice.class.getCanonicalName() + " advice) {this._wraper$beforeAdvice = advice;}", cc);
        cc.addMethod(setBeforeAdvice);
        CtMethod setAfterReturningAdvice = CtMethod.make("public void set$Avice(" + AfterReturningAdvice.class.getCanonicalName() + " advice) {this._wraper$afterReturningAdvice = advice;}", cc);
        cc.addMethod(setAfterReturningAdvice);
        CtMethod setThrowsAdvice = CtMethod.make("public void set$Avice(" + ThrowsAdvice.class.getCanonicalName() + " advice) {this._wraper$throwsAdvice = advice;}", cc);
        cc.addMethod(setThrowsAdvice);
        CtMethod getWraper = CtMethod.make("public " + Object.class.getCanonicalName() + " get$Wraper() {return this;}", cc);
        cc.addMethod(getWraper);
        StringBuilder setProxyedCode = new StringBuilder();
        setProxyedCode.append("public void set$Proxyed(Object proxyed) {")
                .append("this._wraper$target = ")
                .append(InvokerFactory.generateCast("proxyed", Object.class, targetClazz))
                .append(";}");
        CtMethod setProxyed = CtMethod.make(setProxyedCode.toString(), cc);
        cc.addMethod(setProxyed);
    }

    private static boolean proxyMethod(ClassPool pool, CtClass cc, Method method, Set<CtMethod> methodSet) throws NotFoundException, CannotCompileException {
        Class<?> returnClazz = method.getReturnType();
        int paramSize = method.getParameterTypes().length;
        CtMethod cm = createCtMethod(pool, cc, method);
        if (!methodSet.add(cm))
            return false;
        StringBuilder bodyCode = new StringBuilder();
        bodyCode.append("{");
        StringBuilder invorkeCode = new StringBuilder();
        invorkeCode.append("_wraper$target.")
                .append(method.getName())
                .append("(");
        for (int paramIndex = 1; paramIndex < paramSize + 1; paramIndex++) {
            invorkeCode.append("$").append(paramIndex);
            if (paramIndex != paramSize)
                invorkeCode.append(",");
        }
        invorkeCode.append(")");
        if (returnClazz != void.class) {
            bodyCode.append("Object returnValue = ")
                    .append(InvokerFactory.generateCast(invorkeCode.toString(), returnClazz, Object.class))
                    .append(";")
                    .append("return ")
                    .append(InvokerFactory.generateCast("returnValue", Object.class, method.getReturnType()));
        } else {
            bodyCode.append(invorkeCode.toString());
        }
        bodyCode.append(";}");
        //		bodyCode.append(";}");
        cm.setBody(bodyCode.toString());
        cc.addMethod(cm);
        return true;
    }
}

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
package com.tny.game.common.reflect.aop;

import com.tny.game.common.context.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.aop.annotation.*;
import com.tny.game.common.reflect.javassist.*;
import javassist.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * AOP构建对象构建器
 * <p>
 * 会对标记@AOP的类和方法进行AOP代理
 * <p>
 * 进行aop代理的方法至少是protected权限的才会进行代理
 *
 * @param <T>
 * @author KGTny
 */
public class AoperBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AoperBuilder.class);

    /** 键=（目标类，注解集内容）：原按类键控使不同注解集的构建互相吞 */
    private final static ConcurrentMap<AoperBuilder.AopKey, Class<?>> AOPER_MAP = new ConcurrentHashMap<>();

    static final class AopKey {

        private final Class<?> clazz;

        private final Set<Class<? extends Annotation>> annotations;

        AopKey(Class<?> clazz, Set<Class<? extends Annotation>> annotations) {
            this.clazz = clazz;
            this.annotations = new java.util.HashSet<>(annotations);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AopKey)) {
                return false;
            }
            AopKey that = (AopKey) o;
            return clazz == that.clazz && annotations.equals(that.annotations);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(clazz, annotations);
        }
    }

    private final static String PROXY_CLASS_NAME = ".AOPerProxy$$";

    private BeforeAdvice beforeAdvice;

    private AfterReturningAdvice afterReturningAdvice;

    private ThrowsAdvice throwsAdvice;

    private Class<T> clazz;

    private Set<Class<? extends Annotation>> aopAnnotationSet = new HashSet<>();

    public static <C> AoperBuilder<C> newBuilder(Class<C> clazz) {
        return new AoperBuilder<>(clazz);
    }

    private AoperBuilder(Class<T> clazz) {
        this.clazz = clazz;
    }

    public AoperBuilder<T> setBeforeAdvice(BeforeAdvice beforeAdvice) {
        this.beforeAdvice = beforeAdvice;
        return this;
    }

    public AoperBuilder<T> setAfterReturningAdvice(AfterReturningAdvice afterReturningAdvice) {
        this.afterReturningAdvice = afterReturningAdvice;
        return this;
    }

    public AoperBuilder<T> addAnnotation(Class<? extends Annotation> anClass) {
        this.aopAnnotationSet.add(anClass);
        return this;
    }

    public AoperBuilder<T> addAnnotation(Collection<Class<? extends Annotation>> anClassColl) {
        this.aopAnnotationSet.addAll(anClassColl);
        return this;
    }

    public AoperBuilder<T> setThrowsAdvice(ThrowsAdvice throwsAdvice) {
        this.throwsAdvice = throwsAdvice;
        return this;
    }

    public T build() {
        // 失败显式化（原返回 null：NPE 在远离成因的调用点才爆）
        Class<Aoper<T>> aopClass = getAOPerClass(this.clazz, this.aopAnnotationSet);
        if (aopClass == null) {
            throw new IllegalStateException("AOP 代理类生成失败: " + this.clazz);
        }
        try {
            Aoper<T> aop = aopClass.getDeclaredConstructor().newInstance();
            aop.set$Avice(this.afterReturningAdvice);
            aop.set$Avice(this.beforeAdvice);
            aop.set$Avice(this.throwsAdvice);
            //			aoper.set$Proxyed(target);
            return aop.get$Wraper();
        } catch (Exception e) {
            throw new IllegalStateException("AOP 代理实例化失败: " + this.clazz, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<Aoper<T>> getAOPerClass(Class<?> targetClass, Set<Class<? extends Annotation>> aopAnnotationSet) {
        AopKey cacheKey = new AopKey(targetClass, aopAnnotationSet);
        Class<?> aoperClass = AOPER_MAP.get(cacheKey);
        if (aoperClass != null) {
            return (Class<Aoper<T>>) aoperClass;
        }
        ClassPool pool = ClassPool.getDefault();
        try {
            synchronized (targetClass) {
                aoperClass = AOPER_MAP.get(cacheKey);
                if (aoperClass != null) {
                    return (Class<Aoper<T>>) aoperClass;
                }
                String proxyClassName = targetClass.getPackage().getName() + PROXY_CLASS_NAME + targetClass.getSimpleName();
                /* 获得DProxy类作为代理类的父类 */
                CtClass proxyClass = pool.makeClass(proxyClassName);
                CtClass superclass = pool.get(targetClass.getCanonicalName());
                proxyClass.setSuperclass(superclass);
                CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, proxyClass);
                ctConstructor.setBody("{super();}");
                proxyClass.addConstructor(ctConstructor);
                implementAOPer(pool, targetClass, proxyClass);
                CtField staticMethods = CtField.make("protected static java.lang.reflect.Method [] _aoper$METHODS = null;", proxyClass);
                proxyClass.addField(staticMethods);
                CtField staticLogger = CtField
                        .make("protected static final org.slf4j.Logger _aoper$LOGGER = org.slf4j.LoggerFactory.getLogger(\"AOP\");", proxyClass);
                proxyClass.addField(staticLogger);
                AOP globalAOP = targetClass.getAnnotation(AOP.class);
                List<Method> allMethodList = ReflectAide.getDeepMethod(targetClass);
                List<Method> aopMethodList = new ArrayList<>(allMethodList.size());
                int methodIndex = 0;
                Set<CtMethod> methodSet = new HashSet<>();
                for (Method method : allMethodList) {
                    int modifiers = method.getModifiers();
                    if ((Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) && !Modifier.isFinal(modifiers) &&
                        !Modifier.isStatic(modifiers)) {
                        if (isAop(globalAOP, method, aopAnnotationSet)) {
                            proxyMethod(pool, proxyClass, method, methodIndex, methodSet);
                            aopMethodList.add(method);
                            methodIndex++;
                        }
                        //					else {
                        //						proxyMethod(pool, proxyClass, method, methodSet);
                        //					}
                    }
                }
                aoperClass = proxyClass.toClass(targetClass);
                Field methodsField = aoperClass.getDeclaredField("_aoper$METHODS");
                methodsField.setAccessible(true);
                methodsField.set(null, aopMethodList.toArray(new Method[0]));
                Class<?> old = AOPER_MAP.putIfAbsent(cacheKey, aoperClass);
                return (Class<Aoper<T>>) (old != null ? old : aoperClass);
            }
        } catch (Exception e) {
            LOGGER.error("生成 {} 代理类错误", targetClass, e);
            // 留痕：日志保留、成因随显式异常上抛（原吞异常返回 null，build 抛出的 IllegalStateException 无 cause）
            throw new IllegalStateException("AOP 代理类生成失败: " + targetClass, e);
        }
    }

    protected static boolean proxyMethod(ClassPool pool, CtClass cc, Method method, Set<CtMethod> methodSet)
            throws NotFoundException, CannotCompileException {
        Class<?> returnClazz = method.getReturnType();
        int paramSize = method.getParameterTypes().length;
        CtMethod cm = createCtMethod(pool, cc, method);
        if (!methodSet.add(cm)) {
            return false;
        }
        StringBuilder bodyCode = new StringBuilder();
        bodyCode.append("{");
        StringBuilder invorkeCode = new StringBuilder();
        invorkeCode.append("super.")
                .append(method.getName())
                .append("(");
        for (int paramIndex = 1; paramIndex < paramSize + 1; paramIndex++) {
            invorkeCode.append("$").append(paramIndex);
            if (paramIndex != paramSize) {
                invorkeCode.append(",");
            }
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

    public static CtMethod createCtMethod(ClassPool pool, CtClass cc, Method method) throws NotFoundException {
        Class<?> returnClazz = method.getReturnType();
        CtClass returnCC = returnClazz != null ? pool.get(returnClazz.getCanonicalName()) : null;
        List<CtClass> paramCCs = new ArrayList<>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return new CtMethod(returnCC, method.getName(), paramCCs.toArray(new CtClass[0]), cc);
    }

    private static void implementAOPer(ClassPool pool, Class<?> targetClazz, CtClass cc) throws NotFoundException, CannotCompileException {
        cc.setInterfaces(new CtClass[]{pool.get(Aoper.class.getCanonicalName())});
        CtField beforeAdvice = CtField.make("private " + BeforeAdvice.class.getCanonicalName() + " _aoper$beforeAdvice;", cc);
        cc.addField(beforeAdvice);
        CtField afterReturningAdvice = CtField.make("private " + AfterReturningAdvice.class.getCanonicalName() + " _aoper$afterReturningAdvice;",
                cc);
        cc.addField(afterReturningAdvice);
        CtField throwsAdvice = CtField.make("private " + ThrowsAdvice.class.getCanonicalName() + " _aoper$throwsAdvice;", cc);
        cc.addField(throwsAdvice);
        CtField attributes = CtField.make("private volatile " + Attributes.class.getCanonicalName() + " _aoper$attributes;", cc);
        cc.addField(attributes);
        CtMethod setBeforeAdvice = CtMethod
                .make("public void set$Avice(" + BeforeAdvice.class.getCanonicalName() + " advice) {this._aoper$beforeAdvice = advice;}", cc);
        cc.addMethod(setBeforeAdvice);
        CtMethod setAfterReturningAdvice = CtMethod.make("public void set$Avice(" + AfterReturningAdvice.class.getCanonicalName() +
                                                         " advice) {this._aoper$afterReturningAdvice = advice;}", cc);
        cc.addMethod(setAfterReturningAdvice);
        CtMethod setThrowsAdvice = CtMethod
                .make("public void set$Avice(" + ThrowsAdvice.class.getCanonicalName() + " advice) {this._aoper$throwsAdvice = advice;}", cc);
        cc.addMethod(setThrowsAdvice);
        CtMethod getWraper = CtMethod.make("public " + Object.class.getCanonicalName() + " get$Wraper() {return this;}", cc);
        cc.addMethod(getWraper);
        CtMethod getAttributes = CtMethod.make("public " + Attributes.class.getCanonicalName() + " get$Attributes() {"
                                               + " if (this._aoper$attributes != null)"
                                               + "		return this._aoper$attributes;"
                                               + "	synchronized (this) { "
                                               + "		if (this._aoper$attributes != null) "
                                               + "			return this._aoper$attributes;"
                                               + "		this._aoper$attributes = " + ContextAttributes.class.getCanonicalName() + ".create();"
                                               + " }"
                                               + " return this._aoper$attributes;"
                                               + "}", cc);
        cc.addMethod(getAttributes);
    }

    private static boolean proxyMethod(ClassPool pool, CtClass cc, Method method, int methodIndex, Set<CtMethod> methodSet)
            throws NotFoundException, CannotCompileException {
        Class<?> returnClazz = method.getReturnType();
        int paramSize = method.getParameterTypes().length;
        CtMethod cm = createCtMethod(pool, cc, method);
        if (!methodSet.add(cm)) {
            return false;
        }
        StringBuilder bodyCode = new StringBuilder();
        bodyCode.append("{java.lang.reflect.Method method = _aoper$METHODS[")
                .append(methodIndex)
                .append("];")
                .append("Object[] params = new Object[")
                .append(paramSize)
                .append("];")
                .append("try {")
                .append("if (_aoper$beforeAdvice != null)")
                .append("_aoper$beforeAdvice.doBefore(method, $args, this);")
                .append("} catch (Throwable e) {_aoper$LOGGER.error(\"beforeAdvice.before exception\", e);}")
                .append("Object returnValue = null;")
                .append("try {");
        StringBuilder invorkeCode = new StringBuilder();
        invorkeCode.append("super.")
                .append(method.getName())
                .append("(");
        for (int paramIndex = 1; paramIndex < paramSize + 1; paramIndex++) {
            invorkeCode.append("$").append(paramIndex);
            if (paramIndex != paramSize) {
                invorkeCode.append(",");
            }
        }
        invorkeCode.append(")");
        if (returnClazz != void.class) {
            bodyCode.append("returnValue = ")
                    .append(InvokerFactory.generateCast(invorkeCode.toString(), returnClazz, Object.class));
        } else {
            bodyCode.append(invorkeCode);
        }
        bodyCode.append(";")
                .append("try {")
                .append("if (_aoper$afterReturningAdvice != null)")
                .append("_aoper$afterReturningAdvice.doAfterReturning(returnValue, method, $args, this);")
                .append("} catch (Throwable e) {_aoper$LOGGER.error(\"afterReturningAdvice.afterReturning exception\", e);}");
        if (returnClazz != void.class) {
            bodyCode.append("return ")
                    .append(InvokerFactory.generateCast("returnValue", Object.class, method.getReturnType()))
                    .append(";");
        }
        bodyCode.append("} catch (Throwable invorkException) {")
                .append("try {")
                .append("if (_aoper$throwsAdvice != null)")
                .append("_aoper$throwsAdvice.doAfterThrowing(method, $args, this, invorkException);")
                .append("} catch (Throwable e) {_aoper$LOGGER.error(\"throwsAdvice.afterThrowing exception\", e);}")
                .append("throw invorkException;}");
        bodyCode.append("}");
        //		System.out.println(bodyCode);
        cm.setBody(bodyCode.toString());
        cc.addMethod(cm);
        return true;
    }

    private static boolean isAop(AOP globalAOP, Method method, Set<Class<? extends Annotation>> aopAnnotationSet) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            return false;
        }
        if (globalAOP != null) {
            // 类级注解声明的可见性集合必须生效（原遍历全量枚举，@AOP 值参数形同虚设）
            for (Privileges privilege : globalAOP.value()) {
                if (privilege.check(method)) {
                    return true;
                }
            }
            return false;
        } else {
            for (Class<? extends Annotation> clazz : aopAnnotationSet) {
                if (method.getAnnotation(clazz) != null) {
                    return true;
                }
            }
            return method.getAnnotation(AOP.class) != null;
        }
    }

}

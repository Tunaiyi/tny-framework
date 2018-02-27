package com.tny.game.common.reflect.javassist;

import com.tny.game.common.collection.ConcurrentHashSet;
import com.tny.game.common.context.*;
import com.tny.game.common.reflect.ReflectAide;
import javassist.*;

import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.concurrent.*;

public class InvokerFactory {
    /**
     * 调用器池
     */
    final private static ConcurrentMap<Object, MethodInvoker> INVOKER_MAP = new ConcurrentHashMap<>();
    final private static ConcurrentHashSet<Object> TEST_SET = new ConcurrentHashSet<>();

    final private static ConcurrentMap<Constructor<?>, ConstructInvoker> CONSTRUCTOR_MAP = new ConcurrentHashMap<Constructor<?>, ConstructInvoker>();

    private static ClassPool cp = ClassPool.getDefault();

    static {
        ClassClassPath classPath = new ClassClassPath(InvokerFactory.class);
        cp.insertClassPath(classPath);
    }

    /**
     * @param method
     * @return
     */
    public static MethodInvoker newInvoker(Method method) {
        MethodInvoker invoker = INVOKER_MAP.get(method);
        if (invoker != null) {
            return invoker;
        }
        StringBuilder proxyClassNameBuilder = new StringBuilder();
        if (method.getDeclaringClass().getCanonicalName().startsWith("java.util"))
            proxyClassNameBuilder.append(method.getDeclaringClass().getCanonicalName().replace("java.util", "javaproxy.util"));
        else
            proxyClassNameBuilder.append(method.getDeclaringClass().getCanonicalName());
        int hash = method.getDeclaringClass().getName().hashCode() ^ method.getName().hashCode();
        for (Class<?> paramClass : method.getParameterTypes())
            hash ^= paramClass.getName().hashCode();
        proxyClassNameBuilder.append("$");
        proxyClassNameBuilder.append(method.getName());
        proxyClassNameBuilder.append("$");
        proxyClassNameBuilder.append(Math.abs(hash));
        String proxyClassName = proxyClassNameBuilder.toString();
        StringBuilder invokeCode = new StringBuilder();
        try {
            synchronized (method.getDeclaringClass()) {
                Class<?> proxyClass;
                try {
                    Class.forName(proxyClassName);
                } catch (Throwable e) {
                    invoker = INVOKER_MAP.get(method);
                    if (invoker != null)
                        return invoker;
                    TEST_SET.add(method);
                    CtClass cc = cp.makeClass(proxyClassName);

                    cc.addInterface(cp.get(MethodInvoker.class.getCanonicalName()));
                    invokeCode.append("public Object invoke(Object host, Object [] args){");
                    StringBuilder parameterCode = new StringBuilder();
                    for (int i = 0; i < method.getParameterTypes().length; i++) {
                        if (i > 0) {
                            parameterCode.append(",");
                        }
                        Class<?> parameterType = method.getParameterTypes()[i];
                        parameterCode.append(generateCast("args[" + i + "]",
                                Object.class, parameterType));
                    }
                    if (method.getParameterTypes().length > 0) {
                        invokeCode.append("if(args==null||args.length!=");
                        invokeCode.append(method.getParameterTypes().length);
                        invokeCode.append(")throw new IllegalArgumentException(\"wrong number of arguments\");");
                    }

                    StringBuilder executeCode = new StringBuilder();

                    executeCode.append("((");
                    executeCode.append(method.getDeclaringClass().getCanonicalName());
                    executeCode.append(")");
                    String objCode = Modifier.isStatic(method.getModifiers()) ? "" : "host";
                    executeCode.append(objCode);
                    executeCode.append(").");
                    executeCode.append(method.getName());
                    executeCode.append("(");
                    executeCode.append(parameterCode);
                    executeCode.append(")");

                    if (!method.getReturnType().equals(Void.TYPE)) {
                        invokeCode.append("return ");
                        if (method.getReturnType().isPrimitive())
                            invokeCode.append(generateCast(executeCode.toString(), method.getReturnType(), Object.class));
                        else
                            invokeCode.append(executeCode.toString());
                        invokeCode.append(";");
                    } else {
                        invokeCode.append(executeCode.toString());
                        invokeCode.append(";return null;");
                    }
                    invokeCode.append("}");
                    cc.addMethod(CtMethod.make(invokeCode.toString(), cc));
                    proxyClass = cc.toClass();
                    invoker = (MethodInvoker) proxyClass.getConstructor().newInstance();
                    MethodInvoker oldInvoker = INVOKER_MAP.putIfAbsent(method, invoker);
                    if (oldInvoker != null)
                        return oldInvoker;
                    return invoker;
                }
                return INVOKER_MAP.get(method);
            }
        } catch (Throwable e) {
            throw new RuntimeException(MessageFormat.format("编译{0}.{1}异常 \n{2}", method.getDeclaringClass(), method, invokeCode.toString()), e);
        }
    }

    public static ConstructInvoker newConstructor(Constructor<?> constructor) {
        ConstructInvoker invoker = CONSTRUCTOR_MAP.get(constructor);
        if (invoker != null)
            return invoker;
        StringBuilder proxyClassNameBuilder = new StringBuilder();
        proxyClassNameBuilder.append(constructor.getDeclaringClass().getCanonicalName());
        proxyClassNameBuilder.append("$Constructor$");
        proxyClassNameBuilder.append(Math.abs(constructor.hashCode()));
        String proxyClassName = proxyClassNameBuilder.toString();
        StringBuilder invokeCode = new StringBuilder();
        try {
            synchronized (constructor) {
                Class<?> proxyClass;
                try {
                    proxyClass = Class.forName(proxyClassName);
                } catch (Throwable e) {
                    invoker = CONSTRUCTOR_MAP.get(constructor);
                    if (invoker != null)
                        return invoker;
                    CtClass cc = cp.makeClass(proxyClassName);

                    cc.addInterface(cp.get(ConstructInvoker.class.getName()));
                    invokeCode.append("public Object newInstance(Object[] args){");
                    StringBuilder parameterCode = new StringBuilder();
                    for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                        if (i > 0) {
                            parameterCode.append(",");
                        }
                        Class<?> parameterType = constructor.getParameterTypes()[i];
                        parameterCode.append(generateCast("args[" + i + "]", Object.class, parameterType));
                    }
                    if (constructor.getParameterTypes().length > 0) {
                        invokeCode.append("if(args==null||args.length!=");
                        invokeCode.append(constructor.getParameterTypes().length);
                        invokeCode.append(")throw new IllegalArgumentException(\"wrong number of arguments\");");
                    }

                    invokeCode.append("return new ");
                    invokeCode.append(constructor.getDeclaringClass().getCanonicalName());
                    invokeCode.append("(");
                    invokeCode.append(parameterCode);
                    invokeCode.append(");");

                    invokeCode.append("}");
                    cc.addMethod(CtMethod.make(invokeCode.toString(), cc));
                    proxyClass = cc.toClass();
                    invoker = (ConstructInvoker) proxyClass.getConstructor().newInstance();
                    ConstructInvoker oldInvoker = CONSTRUCTOR_MAP.putIfAbsent(constructor, invoker);
                    if (oldInvoker != null)
                        return oldInvoker;
                    return invoker;
                }
                return CONSTRUCTOR_MAP.get(constructor);
            }
        } catch (Throwable e) {
            throw new RuntimeException(MessageFormat.format("编译{0}.{1}异常 \n{2}", constructor.getDeclaringClass(), constructor, invokeCode.toString()), e);
        }
    }

    public static String generateCast(String arg, Class<?> fromClass, Class<?> toClass) {
        StringBuilder cast = new StringBuilder();
        if (fromClass.isPrimitive() && !toClass.isPrimitive()) {
            Class<?> wraperClass = toClass;
            if (!isWraper(toClass)) {
                wraperClass = getWraper(fromClass);
            }
            cast.append("(");
            cast.append(toClass.getCanonicalName());
            cast.append(")");
            cast.append(wraperClass.getCanonicalName());
            cast.append(".valueOf((");
            cast.append(getPrimitive(wraperClass).getCanonicalName());
            cast.append(")");
            cast.append(arg);
            cast.append(")");
        } else if (!fromClass.isPrimitive() && toClass.isPrimitive()) {
            cast.append("(");
            cast.append(toClass.getCanonicalName());
            cast.append(")");
            Class<?> wraperClass = fromClass;
            if (!isWraper(fromClass)) {
                wraperClass = getWraper(toClass);
                cast.append("((");
                if (Number.class.isAssignableFrom(wraperClass)) {
                    cast.append(Number.class.getCanonicalName());
                } else {
                    cast.append(wraperClass.getCanonicalName());
                }
                cast.append(")");
                cast.append(arg);
                cast.append(")");
            } else {
                cast.append(arg);
            }
            cast.append(".");
            cast.append(getPrimitive(wraperClass).getCanonicalName());
            cast.append("Value()");
        } else {
            cast.append("(");
            cast.append(toClass.getCanonicalName());
            cast.append(")");
            cast.append(arg);
        }
        return cast.toString();
    }

    private static Class<?> getPrimitive(Class<?> wraperClass) {
        if (wraperClass.equals(Integer.class)) {
            return Integer.TYPE;
        }
        if (wraperClass.equals(Short.class)) {
            return Short.TYPE;
        }
        if (wraperClass.equals(Long.class)) {
            return Long.TYPE;
        }
        if (wraperClass.equals(Float.class)) {
            return Float.TYPE;
        }
        if (wraperClass.equals(Double.class)) {
            return Double.TYPE;
        }
        if (wraperClass.equals(Byte.class)) {
            return Byte.TYPE;
        }
        if (wraperClass.equals(Character.class)) {
            return Character.TYPE;
        }
        if (wraperClass.equals(Boolean.class)) {
            return Boolean.TYPE;
        }
        if (wraperClass.equals(Void.class)) {
            return Void.TYPE;
        }
        return wraperClass;
    }

    private static Class<?> getWraper(Class<?> toClass) {
        if (toClass.equals(Integer.TYPE)) {
            return Integer.class;
        }
        if (toClass.equals(Short.TYPE)) {
            return Short.class;
        }
        if (toClass.equals(Long.TYPE)) {
            return Long.class;
        }
        if (toClass.equals(Float.TYPE)) {
            return Float.class;
        }
        if (toClass.equals(Double.TYPE)) {
            return Double.class;
        }
        if (toClass.equals(Byte.TYPE)) {
            return Byte.class;
        }
        if (toClass.equals(Character.TYPE)) {
            return Character.class;
        }
        if (toClass.equals(Boolean.TYPE)) {
            return Boolean.class;
        }
        if (toClass.equals(Void.TYPE)) {
            return Void.class;
        }
        return toClass;
    }

    private static boolean isWraper(Class<?> toClass) {
        if (toClass.equals(Integer.class)) {
            return true;
        }
        if (toClass.equals(Short.class)) {
            return true;
        }
        if (toClass.equals(Long.class)) {
            return true;
        }
        if (toClass.equals(Float.class)) {
            return true;
        }
        if (toClass.equals(Double.class)) {
            return true;
        }
        if (toClass.equals(Byte.class)) {
            return true;
        }
        if (toClass.equals(Character.class)) {
            return true;
        }
        if (toClass.equals(Boolean.class)) {
            return true;
        }
        if (toClass.equals(Void.class)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Method target = null;
        for (Method method : ReflectAide.getDeepMethod(ContextAttributes.class)) {
            if (method.getName().equals("getMap")) {
                target = method;
                break;
            }
        }
        Attributes attributes = ContextAttributes.create();
        MethodInvoker invoker = InvokerFactory.newInvoker(target);
        System.out.println(invoker.getClass());
        System.out.println(invoker.invoke(attributes));
    }
}
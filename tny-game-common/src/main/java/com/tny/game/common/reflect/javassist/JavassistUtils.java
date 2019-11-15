package com.tny.game.common.reflect.javassist;

import javassist.*;

import java.lang.reflect.Method;
import java.util.*;

public class JavassistUtils {

    public static CtMethod getMethodBy(Class<?> clazz, Method method) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        return getCtMethod(clazz, method, pool);
    }

    public static CtMethod getMethodBy(ClassPool pool, Class<?> clazz, Method method) throws NotFoundException {
        return getCtMethod(clazz, method, pool);
    }

    public static CtMethod getMethodBy(ClassPool pool, CtClass ctClass, Method method) throws NotFoundException {
        return getCtMethod(pool, ctClass, method);
    }

    private static CtMethod getCtMethod(Class<?> clazz, Method method, ClassPool pool) throws NotFoundException {
        CtClass ctClass = pool.getCtClass(clazz.getName());
        return getCtMethod(pool, ctClass, method);
    }

    private static CtMethod getCtMethod(ClassPool pool, CtClass ctClass, Method method) throws NotFoundException {
        List<CtClass> paramCCs = new ArrayList<>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return ctClass.getDeclaredMethod(method.getName(), paramCCs.toArray(new CtClass[0]));
    }
}

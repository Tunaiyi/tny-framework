package com.tny.game.common.reflect.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JavassistUtils {

    public static CtMethod getMethodBy(Class<?> clazz, Method method) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.getCtClass(clazz.getName());
        List<CtClass> paramCCs = new ArrayList<CtClass>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return ctClass.getDeclaredMethod(method.getName(), paramCCs.toArray(new CtClass[0]));
    }

    public static CtMethod getMethodBy(ClassPool pool, Class<?> clazz, Method method) throws NotFoundException {
        CtClass ctClass = pool.getCtClass(clazz.getName());
        List<CtClass> paramCCs = new ArrayList<CtClass>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return ctClass.getDeclaredMethod(method.getName(), paramCCs.toArray(new CtClass[0]));
    }

    public static CtMethod getMethodBy(ClassPool pool, CtClass ctClass, Method method) throws NotFoundException {
        List<CtClass> paramCCs = new ArrayList<CtClass>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return ctClass.getDeclaredMethod(method.getName(), paramCCs.toArray(new CtClass[0]));
    }
}

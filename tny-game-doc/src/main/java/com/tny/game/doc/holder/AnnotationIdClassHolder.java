package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/5 11:18 下午
 */
public class AnnotationIdClassHolder {

    private static final Map<Class<?>, AnnotationIdClassHolder> HOLDER_MAP = new ConcurrentHashMap<>();

    private final Class<?> annClass;

    private final List<Method> idMethods = new ArrayList<>();

    public static AnnotationIdClassHolder holder(Class<?> annClass) {
        if (annClass.getAnnotation(DocAnnotationClass.class) == null) {
            return null;
        }
        return HOLDER_MAP.computeIfAbsent(annClass, AnnotationIdClassHolder::new);
    }

    private AnnotationIdClassHolder(Class<?> annClass) {
        Method valueMethod = null;
        for (Method method : annClass.getDeclaredMethods()) {
            if (method.getName().equals("value")) {
                valueMethod = method;
            }
            if (method.getAnnotation(DocAnnotationId.class) != null) {
                idMethods.add(method);
            }
        }
        if (valueMethod != null) {
            idMethods.add(valueMethod);
        }
        this.annClass = annClass;
    }

    public Class<?> getAnnClass() {
        return annClass;
    }

    public Object getId(Annotation annotation) {
        for (Method method : idMethods) {
            Object id = ReflectionUtils.invokeMethod(method, annotation);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

}

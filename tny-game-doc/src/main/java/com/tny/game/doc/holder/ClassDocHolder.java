package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

public class ClassDocHolder extends DocClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassDocHolder.class);

    private final Class<?> entityClass;

    private ClassDocHolder(Class<?> clazz) {
        super(clazz);
        this.entityClass = clazz;

    }

    public static ClassDocHolder create(Class<?> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        if (classDoc == null) {
            LOGGER.warn("{} is not classDoc", clazz);
            return null;
        }
        return new ClassDocHolder(clazz);
    }

    private static List<DocMethod> createFunctionList(Class<?> clazz) {
        List<DocMethod> list = new ArrayList<DocMethod>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isBridge()) {
                continue;
            }
            DocMethod holder = DocMethod.create(clazz, method);
            if (holder != null) {
                list.add(holder);
            }
        }
        return list;
    }

    private static List<DocVar> createFieldList(Class<?> clazz) {
        List<DocVar> list = new ArrayList<DocVar>();
        for (Field field : clazz.getDeclaredFields()) {
            DocVar fieldDocHolder = DocVar.create(field);
            if (fieldDocHolder != null) {
                list.add(fieldDocHolder);
            }
        }
        return list;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public List<DocMethod> getFunList() {
        return getMethodList();
    }

}

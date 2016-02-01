package com.tny.game.doc.holder;

import com.tny.game.annotation.Controller;
import com.tny.game.doc.annotation.ClassDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassDocHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassDocHolder.class);

    private ClassDoc classDoc;

    private Class<?> entityClass;

    private int moduleID = -1;

    private List<FieldDocHolder> fieldList;

    private List<FunDocHolder> funList;

    public static ClassDocHolder create(Class<?> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        ClassDocHolder holder = new ClassDocHolder();
        if (classDoc == null) {
            LOGGER.warn("{} is not classDoc", clazz);
            return null;
        }
        Controller controller = clazz.getAnnotation(Controller.class);
        if (controller == null) {
            LOGGER.warn("{} is not controller", clazz);
            return null;
        } else {
            if (controller.value() < 0) {
                LOGGER.warn("{} controller value {} < 0", clazz, controller.value());
            }
            holder.moduleID = controller.value();
        }
        holder.classDoc = classDoc;
        holder.entityClass = clazz;
        holder.fieldList = Collections.unmodifiableList(createFieldList(clazz));
        holder.funList = Collections.unmodifiableList(createFunctionList(clazz));
        return holder;
    }

    private static List<FunDocHolder> createFunctionList(Class<?> clazz) {
        List<FunDocHolder> list = new ArrayList<FunDocHolder>();
        for (Method method : clazz.getDeclaredMethods()) {
            FunDocHolder holder = FunDocHolder.create(clazz, method);
            if (holder != null)
                list.add(holder);
        }
        return list;
    }

    private static List<FieldDocHolder> createFieldList(Class<?> clazz) {
        List<FieldDocHolder> list = new ArrayList<FieldDocHolder>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldDocHolder fieldDocHolder = FieldDocHolder.create(field);
            if (fieldDocHolder != null)
                list.add(fieldDocHolder);
        }
        return list;
    }

    public ClassDoc getClassDoc() {
        return classDoc;
    }

    public int getModuleID() {
        return moduleID;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public List<FieldDocHolder> getFieldList() {
        return fieldList;
    }

    public List<FunDocHolder> getFunList() {
        return funList;
    }

}

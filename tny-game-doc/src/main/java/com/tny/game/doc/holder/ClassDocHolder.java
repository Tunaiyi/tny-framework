package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;
import com.tny.game.net.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

public class ClassDocHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassDocHolder.class);

    private ClassDoc classDoc;

    private Class<?> entityClass;

    private String className;

    private List<FieldDocHolder> fieldList;

    private List<FunDocHolder> funList;

    public static ClassDocHolder create(Class<?> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        ClassDocHolder holder = new ClassDocHolder();
        if (classDoc == null) {
            LOGGER.warn("{} is not classDoc", clazz);
            return null;
        }
        RpcController controller = clazz.getAnnotation(RpcController.class);
        if (controller == null) {
            LOGGER.warn("{} is not controller", clazz);
            return null;
        }
        holder.classDoc = classDoc;
        holder.entityClass = clazz;
        holder.fieldList = Collections.unmodifiableList(createFieldList(clazz));
        holder.funList = Collections.unmodifiableList(createFunctionList(clazz));
        holder.className = classDoc.name();
        if (StringUtils.isBlank(holder.className)) {
            holder.className = clazz.getSimpleName();
        }
        return holder;
    }

    private static List<FunDocHolder> createFunctionList(Class<?> clazz) {
        List<FunDocHolder> list = new ArrayList<FunDocHolder>();
        for (Method method : clazz.getDeclaredMethods()) {
            FunDocHolder holder = FunDocHolder.create(clazz, method);
            if (holder != null) {
                list.add(holder);
            }
        }
        return list;
    }

    private static List<FieldDocHolder> createFieldList(Class<?> clazz) {
        List<FieldDocHolder> list = new ArrayList<FieldDocHolder>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldDocHolder fieldDocHolder = FieldDocHolder.create(field);
            if (fieldDocHolder != null) {
                list.add(fieldDocHolder);
            }
        }
        return list;
    }

    public ClassDoc getClassDoc() {
        return classDoc;
    }

    public String getClassName() {
        return this.className;
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

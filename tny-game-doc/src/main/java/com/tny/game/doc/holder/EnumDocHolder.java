package com.tny.game.doc.holder;

import com.tny.game.common.utils.*;
import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Field;
import java.util.*;

public class EnumDocHolder extends DocClass {

    private final Class<?> entityClass;

    private final List<DocVar> enumList;

    private <E extends Enum<E>> EnumDocHolder(Class<E> clazz) {
        super(clazz);
        this.entityClass = clazz;
        this.enumList = Collections.unmodifiableList(createFieldList(clazz));
    }

    public static <E extends Enum<E>> EnumDocHolder create(Class<E> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        Asserts.checkNotNull(classDoc, "{} is not classDoc", clazz);
        return new EnumDocHolder(clazz);
    }

    private static <E extends Enum<E>> List<DocVar> createFieldList(Class<E> clazz) {
        List<DocVar> list = new ArrayList<>();
        for (Enum<?> enumObject : EnumUtils.getEnumList(clazz)) {
            try {
                Field enumField = clazz.getDeclaredField(enumObject.name());
                DocVar fieldDocHolder = DocVar.create(enumField);
                if (fieldDocHolder != null) {
                    list.add(fieldDocHolder);
                }
            } catch (SecurityException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public List<DocVar> getEnumList() {
        return this.enumList;
    }

}

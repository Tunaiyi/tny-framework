package com.tny.game.doc.holder;

import com.tny.game.common.utils.*;
import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Field;
import java.util.*;

public class EnumDocClass extends DocClass {

    private final List<DocField> enumList;

    private <E extends Enum<E>> EnumDocClass(Class<E> clazz) {
        super(clazz);
        this.enumList = Collections.unmodifiableList(createEnumFieldList(clazz));
    }

    public static <E extends Enum<E>> EnumDocClass createEnumClass(Class<E> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        Asserts.checkNotNull(classDoc, "{} is not classDoc", clazz);
        return new EnumDocClass(clazz);
    }

    private static <E extends Enum<E>> List<DocField> createEnumFieldList(Class<E> clazz) {
        List<DocField> list = new ArrayList<>();
        for (Enum<?> enumObject : EnumUtils.getEnumList(clazz)) {
            try {
                Field enumField = clazz.getDeclaredField(enumObject.name());
                DocField fieldDocHolder = DocField.create(enumField);
                if (fieldDocHolder != null) {
                    list.add(fieldDocHolder);
                }
            } catch (SecurityException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public List<DocField> getEnumList() {
        return this.enumList;
    }

}

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
package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

public class DocField extends DocVar implements DocFieldAccess {

    private String name;

    private Field field;

    private Object fieldId;

    private Class<?> ownerClass;

    private Annotation fieldIdAnnotation;

    private DocField() {
        super();
    }

    private DocField(Class<?> ownerClass, Field field) {
        this(ownerClass, field, null, null);
    }

    /**
     * @param field             字段
     * @param fieldIdAnnotation 字段 id
     * @param fieldIdGetter     字段 id 获取器
     */
    private <F extends Annotation> DocField(Class<?> ownerClass, Field field, Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        this.setVarDoc(field.getAnnotation(VarDoc.class), field.getType(), field.getGenericType());
        this.field = field;
        this.ownerClass = ownerClass;
        this.name = field.getName();
        if (fieldIdAnnotation != null) {
            F idAnnotation = field.getAnnotation(fieldIdAnnotation);
            if (fieldIdGetter != null && idAnnotation != null) {
                this.fieldId = fieldIdGetter.apply(idAnnotation);
            }
            this.fieldIdAnnotation = idAnnotation;
        }
    }

    public static DocField create(Class<?> ownerClass, Field field) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        return new DocField(ownerClass, field);
    }

    public static <F extends Annotation> DocField create(Class<?> ownerClass, Field field,
            Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        return new DocField(ownerClass, field, fieldIdAnnotation, fieldIdGetter);
    }

    @Override
    public Object getFieldId() {
        return fieldId;
    }

    public Class<?> getOwnerClass() {
        return ownerClass;
    }

    @Override
    public Annotation getFieldIdAnnotation() {
        return fieldIdAnnotation;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isHasAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return this.getField().getAnnotation(annotationClass) != null;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Annotation getAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return this.getField().getAnnotation(annotationClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}

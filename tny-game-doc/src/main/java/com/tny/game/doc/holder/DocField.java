/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

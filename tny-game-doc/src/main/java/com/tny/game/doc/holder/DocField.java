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

    private Annotation fieldIdAnnotation;

    private DocField() {
        super();
    }

    private DocField(Field field) {
        this(field, null, null);
    }

    /**
     * @param field             字段
     * @param fieldIdAnnotation 字段 id
     * @param fieldIdGetter     字段 id 获取器
     */
    private <F extends Annotation> DocField(Field field, Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        this.setVarDoc(field.getAnnotation(VarDoc.class), field.getType(), field.getGenericType());
        this.field = field;
        this.name = field.getName();
        if (fieldIdAnnotation != null) {
            F idAnnotation = field.getAnnotation(fieldIdAnnotation);
            if (fieldIdGetter != null && idAnnotation != null) {
                this.fieldId = fieldIdGetter.apply(idAnnotation);
            }
            this.fieldIdAnnotation = idAnnotation;
        }
    }

    public static DocField create(Field field) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        return new DocField(field);
    }

    public static <F extends Annotation> DocField create(Field field,
            Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        return new DocField(field, fieldIdAnnotation, fieldIdGetter);
    }

    @Override
    public Object getFieldId() {
        return fieldId;
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

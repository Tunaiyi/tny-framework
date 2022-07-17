package com.tny.game.doc.dto;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

public class FieldDescription {

    private final String className;

    private final String rawClassName;

    private final String fieldName;

    private final String des;

    private final int fieldID;

    private final DocVar holder;

    public FieldDescription(DocVar holder, TypeFormatter typeFormatter) {
        this.holder = holder;
        this.fieldName = holder.getField().getName();
        this.des = holder.getVarDoc().value();
        this.className = typeFormatter.format(holder.getField().getGenericType());
        this.rawClassName = LangTypeFormatter.RAW.format(holder.getField().getGenericType());
        Object idValue = holder.getId();
        if (idValue == null) {
            throw new IllegalArgumentException(
                    format("{} 类 {} 字段ID为 null", holder.getField().getDeclaringClass(), this.fieldName));
        }
        if (idValue instanceof Integer) {
            int intValue = (int)idValue;
            if (intValue <= 0) {
                throw new IllegalArgumentException(
                        format("{} 类 {} 字段ID = {} <= 0", holder.getField().getDeclaringClass(), this.fieldName, intValue));
            }
            this.fieldID = intValue;
        } else {
            throw new IllegalArgumentException(
                    format("{} 类 {} 字段ID非 int 值", holder.getField().getDeclaringClass(), this.fieldName));
        }
    }

    public String getClassName() {
        return this.className;
    }

    public String getRawClassName() {
        return rawClassName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public int getFieldId() {
        return this.fieldID;
    }

    public String getDes() {
        return this.des;
    }

    public Field getRawField() {
        return holder.getField();
    }

    public boolean isHasAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return holder.getField().getAnnotation(annotationClass) != null;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Annotation getAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return holder.getField().getAnnotation(annotationClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}

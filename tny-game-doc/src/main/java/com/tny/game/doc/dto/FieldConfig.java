package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

@XStreamAlias("field")
public class FieldConfig {

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String rawClassName;

    @XStreamAsAttribute
    private String fieldName;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    @XStreamAsAttribute
    private int fieldID;

    private FieldDocHolder holder;

    public FieldConfig(FieldDocHolder holder, TypeFormatter typeFormatter) {
        this.holder = holder;
        this.fieldName = holder.getField().getName();
        this.des = holder.getVarDoc().value();
        this.text = holder.getVarDoc().text();
        if (StringUtils.isBlank(this.text)) {
            this.text = this.des;
        }
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

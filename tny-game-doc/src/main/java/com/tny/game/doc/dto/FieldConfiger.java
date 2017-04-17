package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.LogUtils;
import com.tny.game.doc.LangFormatter;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.FieldDocHolder;
import com.tny.game.protoex.annotations.ProtoExField;
import org.apache.commons.lang3.StringUtils;

@XStreamAlias("field")
public class FieldConfiger {

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

    public FieldConfiger(FieldDocHolder holder, TypeFormatter typeFormatter) {
        this.fieldName = holder.getField().getName();
        this.des = holder.getVarDoc().value();
        this.text = holder.getVarDoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
        this.className = typeFormatter.format(holder.getField().getGenericType());
        this.rawClassName = LangFormatter.RAW.format(holder.getField().getGenericType());
        ProtoExField field = holder.getField().getAnnotation(ProtoExField.class);
        if (field != null) {
            if (field.value() <= 0) {
                throw new IllegalArgumentException(LogUtils.format("{} 类 {} 字段ID = {} <= 0", holder.getField().getDeclaringClass(), this.fieldName, field.value()));
            }
            this.fieldID = field.value();
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

    public int getFieldID() {
        return this.fieldID;
    }

    public String getDes() {
        return this.des;
    }

}

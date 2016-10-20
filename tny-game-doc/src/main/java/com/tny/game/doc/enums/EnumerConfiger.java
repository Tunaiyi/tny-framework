package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.doc.annotation.IDDoc;
import com.tny.game.doc.holder.FieldDocHolder;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@XStreamAlias("enumer")
public class EnumerConfiger {

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    @XStreamAsAttribute
    private String ID = "null";

    public EnumerConfiger(FieldDocHolder holder) {
        this.des = holder.getVarDoc().value();
        this.text = holder.getVarDoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
        Field field = holder.getField();
        this.name = field.getName();
        try {
            Enum<?> object = (Enum<?>) field.get(null);
            for (Field enumField : field.getDeclaringClass().getDeclaredFields()) {
                if (!Modifier.isStatic(enumField.getModifiers()) &&
                        enumField.getAnnotation(IDDoc.class) != null) {
                    enumField.setAccessible(true);
                    this.ID = enumField.get(object).toString();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getDes() {
        return des;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

}

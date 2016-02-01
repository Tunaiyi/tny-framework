package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.doc.annotation.IDDoc;
import com.tny.game.doc.holder.FieldDocHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@XStreamAlias("enumer")
public class EnumerConfiger {

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String ID = "null";

    public EnumerConfiger(FieldDocHolder holder) {
        this.des = holder.getVarDoc().value();
        Field field = holder.getField();
        this.name = field.getName();
        try {
            Enum<?> object = (Enum<?>) field.get(null);
            for (Field enunField : field.getDeclaringClass().getDeclaredFields()) {
                if (!Modifier.isStatic(enunField.getModifiers()) &&
                        enunField.getAnnotation(IDDoc.class) != null) {
                    enunField.setAccessible(true);
                    this.ID = enunField.get(object).toString();
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

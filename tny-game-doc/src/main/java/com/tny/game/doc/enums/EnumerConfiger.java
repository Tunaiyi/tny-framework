package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.util.*;

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

    private Map<String, FieldConfiger> attributes = new HashMap<>();

    public EnumerConfiger(FieldDocHolder holder, TypeFormatter typeFormatter) {
        this.des = holder.getVarDoc().value();
        this.text = holder.getVarDoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
        Field field = holder.getField();
        this.name = field.getName();
        try {
            Enum<?> object = (Enum<?>) field.get(null);
            for (Field enumField : field.getDeclaringClass().getDeclaredFields()) {
                if (!Modifier.isStatic(enumField.getModifiers())) {
                    if (enumField.getAnnotation(IDDoc.class) != null) {
                        enumField.setAccessible(true);
                        this.ID = enumField.get(object).toString();
                    } else if (enumField.getAnnotation(VarDoc.class) != null) {
                        enumField.setAccessible(true);
                        VarDoc varDoc = enumField.getAnnotation(VarDoc.class);
                        this.attributes.put(enumField.getName(),
                                new FieldConfiger(enumField.getGenericType(), enumField.get(object), varDoc.value(), typeFormatter));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static class FieldConfiger {

        private String desc;

        private Object value;

        private String className;

        private String rawClassName;

        public FieldConfiger(Type type, Object value, String desc, TypeFormatter typeFormatter) {
            this.desc = desc;
            this.value = value;
            this.className = typeFormatter.format(type);
            this.rawClassName = LangFormatter.RAW.format(type);
        }

        public String getDesc() {
            return desc;
        }

        public Object getValue() {
            return value;
        }

    }

    public String getDes() {
        return des;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return ID;
    }

}

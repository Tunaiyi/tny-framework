package com.tny.game.doc.enumeration;

import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.doc.holder.*;

import java.lang.reflect.*;
import java.util.*;

public class EnumItemDescription {

    private final String name;

    private final String des;

    private String id = "null";

    private final Map<String, EnumItemAttribute> attributes;

    public EnumItemDescription(DocVar holder, TypeFormatter typeFormatter) {
        this.des = holder.getVarDoc().value();
        Field field = holder.getField();
        this.name = holder.getName();
        try {
            Enum<?> object = (Enum<?>)field.get(null);
            Map<String, EnumItemAttribute> attributes = new HashMap<>();
            for (Field enumField : field.getDeclaringClass().getDeclaredFields()) {
                if (!Modifier.isStatic(enumField.getModifiers())) {
                    if (enumField.getAnnotation(IDDoc.class) != null) {
                        enumField.setAccessible(true);
                        this.id = enumField.get(object).toString();
                    } else if (enumField.getAnnotation(VarDoc.class) != null) {
                        enumField.setAccessible(true);
                        VarDoc varDoc = enumField.getAnnotation(VarDoc.class);
                        attributes.put(enumField.getName(),
                                new EnumItemAttribute(enumField.getGenericType(), enumField.get(object), varDoc.value(), typeFormatter));
                    }
                }
            }
            this.attributes = Collections.unmodifiableMap(attributes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static class EnumItemAttribute {

        private final String className;

        private final String rawClassName;

        private final Object value;

        private final String desc;

        public EnumItemAttribute(Type type, Object value, String desc, TypeFormatter typeFormatter) {
            this.desc = desc;
            this.value = value;
            this.className = typeFormatter.format(type);
            this.rawClassName = LangTypeFormatter.RAW.format(type);
        }

        public String getDesc() {
            return desc;
        }

        public Object getValue() {
            return value;
        }

        public String getClassName() {
            return className;
        }

        public String getRawClassName() {
            return rawClassName;
        }

    }

    public String getDes() {
        return des;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Map<String, EnumItemAttribute> getAttributes() {
        return attributes;
    }

}

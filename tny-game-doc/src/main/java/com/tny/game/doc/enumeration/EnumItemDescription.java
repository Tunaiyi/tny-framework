package com.tny.game.doc.enumeration;

import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.doc.holder.*;

import java.lang.reflect.*;
import java.util.*;

public class EnumItemDescription extends FieldDescription {

    private String id = "null";

    private final Map<String, EnumItemAttribute> attributes;

    public EnumItemDescription(DocField docField, TypeFormatter typeFormatter) {
        super(docField);
        Field field = docField.getField();
        try {
            Enum<?> object = (Enum<?>)field.get(null);
            Map<String, EnumItemAttribute> attributes = new HashMap<>();
            for (Field enumField : field.getDeclaringClass().getDeclaredFields()) {
                if (!Modifier.isStatic(enumField.getModifiers())) {
                    if (enumField.getAnnotation(IDDoc.class) != null) {
                        enumField.setAccessible(true);
                        this.id = enumField.get(object).toString();
                    } else if (enumField.getAnnotation(VarDoc.class) != null) {
                        DocField docVar = DocField.create(field);
                        enumField.setAccessible(true);
                        VarDoc varDoc = enumField.getAnnotation(VarDoc.class);
                        attributes.put(enumField.getName(),
                                new EnumItemAttribute(docVar, enumField.getGenericType(), enumField.get(object), varDoc.value(), typeFormatter));
                    }
                }
            }
            this.attributes = Collections.unmodifiableMap(attributes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static class EnumItemAttribute extends FieldDescription {

        private final Object value;

        private final String desc;

        public EnumItemAttribute(DocField docVar, Type type, Object value, String desc, TypeFormatter typeFormatter) {
            super(docVar, typeFormatter);
            this.desc = desc;
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public Object getValue() {
            return value;
        }

    }

    /**
     * @return 枚举 id
     */
    public String getId() {
        return id;
    }

    /**
     * @return 枚举项的属性
     */
    public Map<String, EnumItemAttribute> getAttributes() {
        return attributes;
    }

}

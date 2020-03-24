package com.tny.game.doc.holder;

import com.tny.game.common.utils.*;
import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.*;
import org.slf4j.*;

import java.lang.reflect.Field;
import java.util.*;

public class EnumDocHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassDocHolder.class);

    private ClassDoc classDoc;

    private String className;

    private Class<?> entityClass;

    private List<FieldDocHolder> enumList;

    public static <E extends Enum<E>> EnumDocHolder create(Class<E> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        ThrowAide.checkNotNull(classDoc, "{} is not classDoc", clazz);
        EnumDocHolder holder = new EnumDocHolder();
        holder.classDoc = classDoc;
        holder.entityClass = clazz;
        holder.enumList = Collections.unmodifiableList(createFieldList(clazz));
        holder.className = classDoc.name();
        if (StringUtils.isBlank(holder.className))
            holder.className = clazz.getSimpleName();
        return holder;
    }

    private static <E extends Enum<E>> List<FieldDocHolder> createFieldList(Class<E> clazz) {
        List<FieldDocHolder> list = new ArrayList<>();
        for (Enum<?> enumObject : EnumUtils.getEnumList(clazz)) {
            try {
                Field enumField = clazz.getDeclaredField(enumObject.name());
                FieldDocHolder fieldDocHolder = FieldDocHolder.create(enumField);
                if (fieldDocHolder != null)
                    list.add(fieldDocHolder);
            } catch (SecurityException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public ClassDoc getClassDoc() {
        return this.classDoc;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }


    public List<FieldDocHolder> getEnumList() {
        return this.enumList;
    }

    public String getClassName() {
        return this.className;
    }
}

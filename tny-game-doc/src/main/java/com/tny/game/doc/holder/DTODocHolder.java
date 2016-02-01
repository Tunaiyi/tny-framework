package com.tny.game.doc.holder;

import com.tny.game.common.reflect.ReflectUtils;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.protoex.annotations.ProtoEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DTODocHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DTODocHolder.class);

    private DTODoc dtoDoc;

    private int id;

    private Class<?> entityClass;

    private List<FieldDocHolder> fieldList;

    public static DTODocHolder create(Class<?> clazz) {
        DTODoc dtoDoc = clazz.getAnnotation(DTODoc.class);
        if (dtoDoc == null) {
            LOGGER.warn("{} is not dtoDoc", clazz);
            return null;
        }
        DTODocHolder holder = new DTODocHolder();
        holder.dtoDoc = dtoDoc;
        holder.entityClass = clazz;
        holder.fieldList = Collections.unmodifiableList(createFieldList(clazz));
        ProtoEx proto = clazz.getAnnotation(ProtoEx.class);
        if (proto != null) {
            holder.id = proto.value();
        }
        return holder;
    }

    private static List<FieldDocHolder> createFieldList(Class<?> clazz) {
        List<FieldDocHolder> list = new ArrayList<FieldDocHolder>();
        for (Field field : ReflectUtils.getDeepField(clazz)) {
            FieldDocHolder fieldDocHolder = FieldDocHolder.create(field);
            if (fieldDocHolder != null)
                list.add(fieldDocHolder);
        }
        return list;
    }

    public DTODoc getDTODoc() {
        return this.dtoDoc;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public int getID() {
        return this.id;
    }

    public List<FieldDocHolder> getFieldList() {
        return this.fieldList;
    }

}

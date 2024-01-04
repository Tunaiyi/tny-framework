/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

public class DTOTableAttribute implements TableAttribute {

    private DTODescription dto;

    private static final AttrKey<Map<Object, DTODescription>> KEY = AttrKeys.key(DTOTableAttribute.class, "ConfigMap");

    @JsonIgnore
    @XStreamOmitField
    private ExportHolder exportHolder;

    @JsonIgnore
    @XStreamOmitField
    private Class<Annotation> classAnnotation;

    @JsonIgnore
    @XStreamOmitField
    private Class<Annotation> fieldAnnotation;

    @JsonIgnore
    @XStreamOmitField
    private Function<Annotation, Object> classIdGetter;

    @JsonIgnore
    @XStreamOmitField
    private Function<Annotation, Object> fieldIdGetter;

    public DTOTableAttribute() {
        super();
    }

    public <C extends Annotation, F extends Annotation> DTOTableAttribute(
            String classAnnotation, Function<C, Object> classIdGetter,
            String fieldAnnotation, Function<F, Object> fieldIdGetter) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            this.classAnnotation = as(loader.loadClass(classAnnotation));
            this.fieldAnnotation = as(loader.loadClass(fieldAnnotation));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        this.classIdGetter = as(classIdGetter);
        this.fieldIdGetter = as(fieldIdGetter);
    }

    public <C extends Annotation, F extends Annotation> DTOTableAttribute(
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        this.classAnnotation = as(classAnnotation);
        this.fieldAnnotation = as(fieldAnnotation);
        this.classIdGetter = as(classIdGetter);
        this.fieldIdGetter = as(fieldIdGetter);
    }

    public DTODescription getDto() {
        return dto;
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
        this.dto = create(Objects.requireNonNull(DTODocClass.create(clazz,
                classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter)), typeFormatter, attributes);
        this.exportHolder = ExportHolder.create(clazz);
    }

    public DTODescription create(DTODocClass holder, TypeFormatter typeFormatter, Attributes attributes) {
        Map<Object, DTODescription> configMap = attributes.computeIfAbsent(KEY, ConcurrentHashMap::new);
        Object id = holder.getId();
        if (id != null) {
            DTODescription configer = configMap.get(holder.getId());
            DTODescription old;
            if (configer != null) {
                if (configer.getDocClassName().equals(holder.getEntityClass().getSimpleName())) {
                    return configer;
                }
                throw new IllegalArgumentException(
                        format("{} 类 与 {} 类 ID 都为 {}", configer.getDocClassName(), holder.getEntityClass(), holder.getId()));
            } else {
                configer = new DTODescription(holder, typeFormatter);
                old = configMap.putIfAbsent(configer.getId(), configer);
                if (old != null) {
                    throw new IllegalArgumentException(
                            format("{} 类 与 {} 类 ID 都为 {}", configer.getDocClassName(), holder.getEntityClass(), holder.getId()));
                } else {
                    return configer;
                }
            }
        }
        throw new IllegalArgumentException(format("{} id 不存在", holder.getEntityClass()));
    }

    @Override
    public String getOutput() {
        return this.exportHolder.getOutput();
    }

    @Override
    public Map<String, Object> getContext() {
        return MapBuilder.<String, Object>newBuilder()
                .put("dto", dto)
                .build();
    }

    @Override
    public String getTemplate() {
        return this.exportHolder.getTemplate();
    }

    @Override
    public String toString() {
        return "DTOTableAttribute{" +
               "dto=" + dto +
               '}';
    }

}

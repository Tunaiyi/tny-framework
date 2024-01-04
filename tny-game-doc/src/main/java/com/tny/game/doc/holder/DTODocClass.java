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

package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public class DTODocClass extends DocClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(DTODocClass.class);

    private final DTODoc dtoDoc;

    private final Object id;

    private final String docDesc;

    private final String docText;

    private final Class<?> entityClass;

    private <C extends Annotation, F extends Annotation> DTODocClass(Class<?> clazz,
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        super(clazz, fieldAnnotation, fieldIdGetter);
        this.dtoDoc = clazz.getAnnotation(DTODoc.class);
        this.docDesc = dtoDoc.value();
        this.docText = dtoDoc.text();
        this.entityClass = clazz;
        C classAnn = clazz.getAnnotation(classAnnotation);
        Object id = classIdGetter.apply(classAnn);
        this.id = id;
        if (id == null) {
            LOGGER.error("{} class id 为 null", clazz);
        }
    }

    public static <C extends Annotation, F extends Annotation> DTODocClass create(Class<?> clazz,
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        DTODoc dtoDoc = clazz.getAnnotation(DTODoc.class);
        if (dtoDoc == null) {
            LOGGER.error("{} 未添加 {} 注解", clazz, DTODoc.class);
            return null;
        }
        C classAnn = clazz.getAnnotation(classAnnotation);
        if (classAnn == null) {
            LOGGER.error("{} 未添加 {} 注解", clazz, classAnnotation);
            return null;
        }
        return new DTODocClass(clazz, classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
    }

    public DTODoc getDTODoc() {
        return this.dtoDoc;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public String getDocDesc() {
        return docDesc;
    }

    @Override
    public String getDocText() {
        return docText;
    }

    public Object getId() {
        return this.id;
    }

}

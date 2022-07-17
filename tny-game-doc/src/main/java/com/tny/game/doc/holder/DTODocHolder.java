package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public class DTODocHolder extends DocClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(DTODocHolder.class);

    private final DTODoc dtoDoc;

    private final Object id;

    private final Class<?> entityClass;

    private <C extends Annotation, F extends Annotation> DTODocHolder(Class<?> clazz,
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        super(clazz, fieldAnnotation, fieldIdGetter);
        this.dtoDoc = clazz.getAnnotation(DTODoc.class);
        this.entityClass = clazz;
        C classAnn = clazz.getAnnotation(classAnnotation);
        Object id = classIdGetter.apply(classAnn);
        this.id = id;
        if (id == null) {
            LOGGER.error("{} class id 为 null", clazz);
        }
    }

    public static <C extends Annotation, F extends Annotation> DTODocHolder create(Class<?> clazz,
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
        return new DTODocHolder(clazz, classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
    }

    public DTODoc getDTODoc() {
        return this.dtoDoc;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public Object getId() {
        return this.id;
    }

}

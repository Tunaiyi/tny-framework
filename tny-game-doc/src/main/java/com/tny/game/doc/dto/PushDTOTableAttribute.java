package com.tny.game.doc.dto;

import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.table.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

public class PushDTOTableAttribute implements TableAttribute {

    private final SortedSet<PushDTODescription> pushDTOList = new TreeSet<>();

    private final Class<Annotation> classAnnotation;

    private final Function<Annotation, Object> classIdGetter;

    private final Class<Annotation> fieldAnnotation;

    private final Function<Annotation, Object> fieldIdGetter;

    public static <C extends Annotation, F extends Annotation> PushDTOTableAttribute create(
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        return new PushDTOTableAttribute(classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
    }

    public <C extends Annotation, F extends Annotation> PushDTOTableAttribute(
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        this.classAnnotation = as(classAnnotation);
        this.classIdGetter = as(classIdGetter);
        this.fieldAnnotation = as(fieldAnnotation);
        this.fieldIdGetter = as(fieldIdGetter);
    }

    public SortedSet<PushDTODescription> getPushDTOSet() {
        return Collections.unmodifiableSortedSet(pushDTOList);
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
        PushDTODescription info = PushDTODescription.create(clazz, classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
        if (info != null) {
            this.pushDTOList.add(info);
        }
    }

    @Override
    public Map<String, Object> getContext() {
        return MapBuilder.<String, Object>newBuilder()
                .put("pushDTOList", pushDTOList)
                .build();
    }

}

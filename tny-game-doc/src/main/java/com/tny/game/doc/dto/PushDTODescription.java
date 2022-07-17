package com.tny.game.doc.dto;

import com.tny.game.doc.holder.*;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import static com.tny.game.common.utils.StringAide.*;

public class PushDTODescription extends ClassDescription {

    private final String handlerName;

    public static <C extends Annotation, F extends Annotation> PushDTODescription create(Class<?> clazz,
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        DTODocHolder holder = DTODocHolder.create(clazz, classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
        if (holder == null) {
            return null;
        }
        return new PushDTODescription(clazz, holder);
    }

    public PushDTODescription(Class<?> clazz, DTODocHolder holder) {
        super(holder);
        this.handlerName = format("public function $send{}$S(dto : {}):void{}", clazz.getSimpleName(), clazz.getSimpleName());
    }

    public String getHandlerName() {
        return handlerName;
    }

}

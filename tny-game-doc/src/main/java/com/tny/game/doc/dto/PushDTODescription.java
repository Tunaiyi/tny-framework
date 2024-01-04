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

import com.tny.game.doc.holder.*;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import static com.tny.game.common.utils.StringAide.*;

public class PushDTODescription extends ClassDescription {

    private final String handlerName;

    public static <C extends Annotation, F extends Annotation> PushDTODescription create(Class<?> clazz,
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        DTODocClass holder = DTODocClass.create(clazz, classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
        if (holder == null) {
            return null;
        }
        return new PushDTODescription(clazz, holder);
    }

    public PushDTODescription(Class<?> clazz, DTODocClass holder) {
        super(holder);
        this.handlerName = format("public function $send{}$S(dto : {}):void{}", clazz.getSimpleName(), clazz.getSimpleName());
    }

    public String getHandlerName() {
        return handlerName;
    }

}

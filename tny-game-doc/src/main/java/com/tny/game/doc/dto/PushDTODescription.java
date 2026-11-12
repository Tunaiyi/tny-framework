/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

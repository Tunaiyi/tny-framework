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

package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import static com.tny.game.common.utils.ObjectAide.*;

public class DocParam extends DocVar implements DocParamAccess {

    private final String name;

    private final Parameter parameter;

    private DocParam(Parameter parameter) {
        super();
        this.setVarDoc(parameter.getAnnotation(VarDoc.class), parameter.getType(), parameter.getParameterizedType());
        this.name = parameter.getName();
        this.parameter = parameter;
    }

    public static DocParam create(Parameter parameter) {
        return new DocParam(parameter);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public boolean isHasAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return this.getParameter().getAnnotation(annotationClass) != null;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Annotation getAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return this.getParameter().getAnnotation(annotationClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}

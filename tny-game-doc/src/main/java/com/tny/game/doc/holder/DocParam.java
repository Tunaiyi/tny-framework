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

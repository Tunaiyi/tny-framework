/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class MethodDescription implements DocMethodAccess {

    private DocMethod docMethod;

    public MethodDescription() {
    }

    public MethodDescription(DocMethod docMethod) {
        this.setDocMethod(docMethod);
    }

    protected MethodDescription setDocMethod(DocMethod docMethod) {
        this.docMethod = docMethod;
        return this;
    }

    public DocMethod getDocMethod() {
        return docMethod;
    }

    @Override
    public FunDoc getFunDoc() {
        return docMethod.getFunDoc();
    }

    @Override
    public String getDocDesc() {
        return docMethod.getDocDesc();
    }

    @Override
    public String getDocText() {
        return docMethod.getDocText();
    }

    @Override
    public String getDocReturnDesc() {
        return docMethod.getDocReturnDesc();
    }

    @Override
    public Method getMethod() {
        return docMethod.getMethod();
    }

    @Override
    public String getMethodName() {
        return docMethod.getMethodName();
    }

    @Override
    public Class<?> getReturnClass() {
        return docMethod.getReturnClass();
    }

    @Override
    public String getReturnClassName() {
        return docMethod.getReturnClassName();
    }

    @Override
    public List<DocParam> getParamList() {
        return docMethod.getParamList();
    }

    @Override
    public boolean isHasAnnotation(String annClass) {
        return docMethod.isHasAnnotation(annClass);
    }

    @Override
    public Annotation getAnnotation(String annClass) {
        return docMethod.getAnnotation(annClass);
    }

}

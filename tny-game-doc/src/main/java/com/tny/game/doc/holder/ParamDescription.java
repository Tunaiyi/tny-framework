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

import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/17 04:26
 **/
public class ParamDescription implements DocParamAccess {

    private DocParam docParam;

    private String docParamClassName;

    public ParamDescription() {
    }

    public ParamDescription(DocParam docParam) {
        this.setDocField(docParam, LangTypeFormatter.RAW);
    }

    public ParamDescription(DocParam docParam, TypeFormatter typeFormatter) {
        this.setDocField(docParam, typeFormatter);
    }

    protected ParamDescription setDocField(DocParam docParam, TypeFormatter typeFormatter) {
        this.docParam = docParam;
        Type type = docParam.getParameter().getParameterizedType();
        this.docParamClassName = typeFormatter.format(type);
        return this;
    }

    @Override
    public VarDoc getVarDoc() {
        return docParam.getVarDoc();
    }

    @Override
    public String getName() {
        return docParam.getName();
    }

    @Override
    public String getDocDesc() {
        return docParam.getDocDesc();
    }

    @Override
    public String getDocText() {
        return docParam.getDocText();
    }

    @Override
    public Parameter getParameter() {
        return docParam.getParameter();
    }

    @Override
    public Class<?> getParamClass() {
        return docParam.getParamClass();
    }

    @Override
    public String getParamClassName() {
        return docParam.getParamClassName();
    }

    public String getDocParamClassName() {
        return docParamClassName;
    }

    @Override
    public boolean isHasAnnotation(String annClass) {
        return docParam.isHasAnnotation(annClass);
    }

    @Override
    public Annotation getAnnotation(String annClass) {
        return docParam.getAnnotation(annClass);
    }

    @Override
    public Class<?> getDocType() {
        return docParam.getDocType();
    }

    @Override
    public String getDocTypeName() {
        return docParam.getDocTypeName();
    }

    @Override
    public String getDocExample() {
        return docParam.getDocExample();
    }

    @Override
    public Class<?> getVarClass() {
        return docParam.getVarClass();
    }

    @Override
    public String getVarClassName() {
        return docParam.getVarClassName();
    }

}

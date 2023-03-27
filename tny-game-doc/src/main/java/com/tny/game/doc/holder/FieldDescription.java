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

import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/17 04:26
 **/
public class FieldDescription implements DocFieldAccess {

    private DocField docField;

    private String docFieldClassName;

    public FieldDescription() {
    }

    public FieldDescription(DocField docField) {
        this.setDocField(docField, LangTypeFormatter.RAW);
    }

    public FieldDescription(DocField docField, TypeFormatter typeFormatter) {
        this.setDocField(docField, typeFormatter);
    }

    private void setDocField(DocField docField, TypeFormatter typeFormatter) {
        this.docField = docField;
        Type type = docField.getField().getGenericType();
        if (type instanceof TypeVariable) {
            ResolvableType resolvableFieldType = ResolvableType.forField(docField.getField(), docField.getOwnerClass());
            this.docFieldClassName = typeFormatter.format(resolvableFieldType.resolve());
        } else if (type instanceof ParameterizedType) {
            ResolvableType resolvableFieldType = ResolvableType.forField(docField.getField(), docField.getOwnerClass());
            var parameterizedTypes = new Type[resolvableFieldType.getGenerics().length];
            int index = 0;
            for (var generic : resolvableFieldType.getGenerics()) {
                parameterizedTypes[index++] = generic.resolve();
            }
            var fieldType = TypeUtils.parameterize(Objects.requireNonNull(resolvableFieldType.resolve()), parameterizedTypes);
            this.docFieldClassName = typeFormatter.format(fieldType);
        } else {
            this.docFieldClassName = typeFormatter.format(type);
        }
    }

    public DocField getDocField() {
        return docField;
    }

    public String getDocFieldClassName() {
        return docFieldClassName;
    }

    @Override
    public Object getFieldId() {
        return docField.getFieldId();
    }

    @Override
    public Annotation getFieldIdAnnotation() {
        return docField.getFieldIdAnnotation();
    }

    @Override
    public Class<?> getFieldClass() {
        return docField.getFieldClass();
    }

    @Override
    public VarDoc getVarDoc() {
        return docField.getVarDoc();
    }

    @Override
    public String getDocDesc() {
        return docField.getDocDesc();
    }

    @Override
    public String getDocText() {
        return docField.getDocText();
    }

    @Override
    public Field getField() {
        return docField.getField();
    }

    @Override
    public String getName() {
        return docField.getName();
    }

    @Override
    public String getFieldClassName() {
        return docField.getFieldClassName();
    }

    @Override
    public boolean isHasAnnotation(String annClass) {
        return docField.isHasAnnotation(annClass);
    }

    @Override
    public Annotation getAnnotation(String annClass) {
        return docField.getAnnotation(annClass);
    }

    @Override
    public Class<?> getDocType() {
        return docField.getDocType();
    }

    @Override
    public String getDocTypeName() {
        return docField.getDocTypeName();
    }

    @Override
    public String getDocExample() {
        return docField.getDocExample();
    }

    @Override
    public Class<?> getVarClass() {
        return docField.getVarClass();
    }

    @Override
    public String getVarClassName() {
        return docField.getVarClassName();
    }

}

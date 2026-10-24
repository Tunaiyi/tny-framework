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

import java.lang.reflect.Type;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/18 21:06
 **/
public class DocVar implements DocVarAccess {

    private VarDoc varDoc;

    private String docText;

    private String docDesc;

    private Class<?> docType;

    private String docTypeName;

    private String docExample;

    private Class<?> varClass;

    private String varClassName;

    public DocVar() {
    }

    public DocVar(VarDoc varDoc, Class<?> varClass, Type varType) {
        this.setVarDoc(varDoc, varClass, varType);
    }

    protected void setVarDoc(VarDoc varDoc, Class<?> varClass, Type varType) {
        this.varDoc = varDoc;
        this.varClass = varClass;
        this.varClassName = varType.getTypeName();
        if (this.varDoc != null) {
            this.docDesc = this.varDoc.value();
            this.docText = this.varDoc.text();
            this.docType = this.varDoc.valueType();
            if (docType != Object.class) {
                this.docTypeName = this.docType.getSimpleName();
            } else {
                this.docTypeName = "";
            }
            this.docExample = this.varDoc.valueExample();
        } else {
            this.docDesc = "";
            this.docText = "";
            this.docType = Object.class;
            this.docTypeName = "";
            this.docExample = "";
        }
    }

    @Override
    public VarDoc getVarDoc() {
        return varDoc;
    }

    @Override
    public String getDocText() {
        return docText;
    }

    @Override
    public String getDocDesc() {
        return docDesc;
    }

    @Override
    public Class<?> getDocType() {
        return docType;
    }

    @Override
    public String getDocTypeName() {
        return docTypeName;
    }

    @Override
    public String getDocExample() {
        return docExample;
    }

    @Override
    public Class<?> getVarClass() {
        return varClass;
    }

    @Override
    public String getVarClassName() {
        return varClassName;
    }

}

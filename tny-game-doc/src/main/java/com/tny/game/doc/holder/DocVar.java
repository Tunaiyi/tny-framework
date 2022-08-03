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

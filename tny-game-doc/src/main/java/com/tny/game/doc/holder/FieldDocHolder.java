package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.VarDoc;

import java.lang.reflect.Field;

public class FieldDocHolder {

    private VarDoc varDoc;

    private Field field;

    private FieldDocHolder() {
        super();
    }

    public static FieldDocHolder create(Field field) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null)
            return null;
        FieldDocHolder holder = new FieldDocHolder();
        holder.varDoc = varDoc;
        holder.field = field;
        return holder;
    }

    public VarDoc getVarDoc() {
        return varDoc;
    }

    public Field getField() {
        return field;
    }

}

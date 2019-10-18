package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoExField;

import java.lang.reflect.Field;

public class FieldDocHolder {

    private VarDoc varDoc;

    private Field field;

    private int id = -1;

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
        ProtoExField protoExField = field.getAnnotation(ProtoExField.class);
        if (protoExField != null)
            holder.id = protoExField.value();
        return holder;
    }

    public int getId() {
        return id;
    }

    public VarDoc getVarDoc() {
        return varDoc;
    }

    public Field getField() {
        return field;
    }

}

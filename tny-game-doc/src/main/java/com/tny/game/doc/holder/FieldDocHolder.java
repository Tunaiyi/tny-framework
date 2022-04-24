package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;

public class FieldDocHolder {

    private VarDoc varDoc;

    private Field field;

    private Object id;

    private FieldDocHolder() {
        super();
    }

    public static FieldDocHolder create(Field field) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        FieldDocHolder holder = new FieldDocHolder();
        holder.varDoc = varDoc;
        holder.field = field;
        return holder;
    }

    public static <F extends Annotation> FieldDocHolder create(Field field,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        FieldDocHolder holder = new FieldDocHolder();
        holder.varDoc = varDoc;
        holder.field = field;
        F fieldAnn = field.getAnnotation(fieldAnnotation);
        if (fieldAnn != null) {
            holder.id = fieldIdGetter.apply(fieldAnn);
        }
        return holder;
    }

    public Object getId() {
        return id;
    }

    public VarDoc getVarDoc() {
        return varDoc;
    }

    public Field getField() {
        return field;
    }

}

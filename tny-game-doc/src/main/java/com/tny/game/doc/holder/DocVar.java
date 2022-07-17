package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;

public class DocVar {

    private Object id;

    private String name;

    private VarDoc varDoc;

    private Field field;

    private Annotation idAnnotation;

    private DocVar() {
        super();
    }

    private DocVar(Field field) {
        this(field, null, null);
    }

    private <F extends Annotation> DocVar(Field field, Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        this.varDoc = field.getAnnotation(VarDoc.class);
        this.field = field;
        this.name = field.getName();
        if (fieldIdAnnotation != null) {
            F idAnnotation = field.getAnnotation(fieldIdAnnotation);
            if (fieldIdGetter != null && idAnnotation != null) {
                this.id = fieldIdGetter.apply(idAnnotation);
            }
            this.idAnnotation = idAnnotation;
        }
    }

    public static DocVar create(Field field) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        return new DocVar(field);
    }

    public static <F extends Annotation> DocVar create(Field field,
            Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        VarDoc varDoc = field.getAnnotation(VarDoc.class);
        if (varDoc == null) {
            return null;
        }
        return new DocVar(field, fieldIdAnnotation, fieldIdGetter);
    }

    public Object getId() {
        return id;
    }

    public Annotation getIdAnnotation() {
        return idAnnotation;
    }

    public VarDoc getVarDoc() {
        return varDoc;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return name;
    }

}

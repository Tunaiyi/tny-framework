package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.reflect.Parameter;

public class ParamDocHolder {

    private VarDoc varDoc;

    private String name;

    private Class<?> varClass;

    private Parameter parameter;

    private ParamDocHolder() {
        super();
    }

    public static ParamDocHolder create(VarDoc varDoc, String name, Class<?> varClass, Parameter parameter) {
        ParamDocHolder holder = new ParamDocHolder();
        holder.varDoc = varDoc;
        holder.name = name;
        holder.varClass = varClass;
        holder.parameter = parameter;
        return holder;
    }

    public VarDoc getVarDoc() {
        return varDoc;
    }

    public String getName() {
        return name;
    }

    public Class<?> getVarClass() {
        return varClass;
    }

    public Parameter getRawParameter() {
        return parameter;
    }

}

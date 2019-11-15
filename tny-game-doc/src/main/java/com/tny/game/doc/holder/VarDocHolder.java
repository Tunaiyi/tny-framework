package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

public class VarDocHolder {

    private VarDoc varDoc;

    private String name;

    private Class<?> varClass;

    private VarDocHolder() {
        super();
    }

    public static VarDocHolder create(VarDoc varDoc, String name, Class<?> varClass) {
        VarDocHolder holder = new VarDocHolder();
        holder.varDoc = varDoc;
        holder.name = name;
        holder.varClass = varClass;
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

}

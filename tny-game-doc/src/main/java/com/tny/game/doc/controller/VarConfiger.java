package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.FieldDocHolder;
import com.tny.game.doc.holder.VarDocHolder;

@XStreamAlias("var")
public class VarConfiger {

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String des;

    public VarConfiger(FieldDocHolder holder, TypeFormatter typeFormatter) {
        this.name = holder.getField().getName();
        this.des = holder.getVarDoc().value();
        this.className = typeFormatter.format(holder.getField().getType());
    }

    public VarConfiger(VarDocHolder holder, TypeFormatter typeFormatter) {
        this.name = holder.getName();
        this.des = holder.getVarDoc().value();
        this.className = typeFormatter.format(holder.getVarClass());
    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }

    public String getClassName() {
        return className;
    }

}

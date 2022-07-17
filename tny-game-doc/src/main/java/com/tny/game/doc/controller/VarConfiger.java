package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.lang.reflect.Parameter;

@XStreamAlias("var")
public class VarConfiger {

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String rawClassName;

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    private final DocParam paramHolder;

    //    public VarConfiger(FieldDocHolder holder, TypeFormatter typeFormatter) {
    //        this.name = holder.getField().getName();
    //        this.className = typeFormatter.format(holder.getField().getType());
    //        this.rawClassName = LangTypeFormatter.RAW.format(holder.getField().getType());
    //        this.des = holder.getVarDoc().value();
    //        this.text = holder.getVarDoc().text();
    //        if (StringUtils.isBlank(this.text)) {
    //            this.text = this.des;
    //        }
    //    }

    public VarConfiger(DocParam holder, TypeFormatter typeFormatter) {
        this.name = holder.getName();
        this.des = holder.getVarDoc().value();
        this.className = typeFormatter.format(holder.getVarClass());
        this.rawClassName = LangTypeFormatter.RAW.format(holder.getVarClass());
        this.paramHolder = holder;
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

    public String getRawClassName() {
        return rawClassName;
    }

    public Parameter getRawParameter() {
        return paramHolder.getRawParameter();
    }

}

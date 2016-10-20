package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.FieldDocHolder;
import com.tny.game.doc.holder.VarDocHolder;
import org.apache.commons.lang3.StringUtils;

@XStreamAlias("var")
public class VarConfiger {

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    public VarConfiger(FieldDocHolder holder, TypeFormatter typeFormatter) {
        this.name = holder.getField().getName();
        this.className = typeFormatter.format(holder.getField().getType());
        this.des = holder.getVarDoc().value();
        this.text = holder.getVarDoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
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

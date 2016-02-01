package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.DTODocHolder;
import com.tny.game.doc.holder.FieldDocHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("dto")
public class DTOConfiger implements Comparable<DTOConfiger> {

    @XStreamAsAttribute
    private String superClassName;

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private int id;

    @XStreamAsAttribute
    private String packageName;

    @XStreamAsAttribute
    private boolean push;

    private FieldList fieldList;

    @XStreamAlias("fieldList")
    private static class FieldList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "field")
        private List<FieldConfiger> fieldList;

    }

    public DTOConfiger(DTODocHolder holder, TypeFormatter typeFormatter) {
        super();
        this.className = holder.getEntityClass().getSimpleName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        this.des = holder.getDTODoc().value();
        this.superClassName = holder.getEntityClass().getSuperclass().getSimpleName();
        this.push = holder.getDTODoc().push();
        this.id = holder.getID();
        this.fieldList = new FieldList();
        List<FieldConfiger> fieldList = new ArrayList<FieldConfiger>();
        for (FieldDocHolder fieldDocHolder : holder.getFieldList()) {
            fieldList.add(new FieldConfiger(fieldDocHolder, typeFormatter));
        }
        this.fieldList.fieldList = Collections.unmodifiableList(fieldList);
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isPush() {
        return push;
    }

    public String getDes() {
        return des;
    }

    public int getID() {
        return id;
    }

    public List<FieldConfiger> getFieldList() {
        return fieldList.fieldList;
    }

    @Override
    public int compareTo(DTOConfiger other) {
        int value = this.packageName.compareTo(other.packageName);
        if (value == 0)
            return this.className.compareTo(other.className);
        return value;
    }

}

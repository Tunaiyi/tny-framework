package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

@XStreamAlias("dto")
public class DTOConfig implements Comparable<DTOConfig> {

    @XStreamAsAttribute
    private String superClassName;

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    @XStreamAsAttribute
    private Object id;

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
        private List<FieldConfig> fieldList;

    }

    public DTOConfig(DTODocHolder holder, TypeFormatter typeFormatter) {
        super();
        this.className = holder.getEntityClass().getSimpleName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        this.des = holder.getDTODoc().value();
        this.superClassName = holder.getEntityClass().getSuperclass().getSimpleName();
        this.push = holder.getDTODoc().push();
        this.id = holder.getId();
        this.fieldList = new FieldList();
        this.text = holder.getDTODoc().text();
        if (StringUtils.isBlank(this.text)) {
            this.text = this.des;
        }
        Map<Integer, FieldConfig> fieldMap = new HashMap<>();
        List<FieldConfig> fieldList = new ArrayList<>();
        for (FieldDocHolder fieldDocHolder : holder.getFieldList()) {
            try {
                FieldConfig conifer = new FieldConfig(fieldDocHolder, typeFormatter);
                fieldList.add(conifer);
                FieldConfig old = fieldMap.put(conifer.getFieldId(), conifer);
                if (old != null) {
                    throw new IllegalArgumentException(format("{} 类 {} 与 {} 字段 ID 都为 {}",
                            holder.getEntityClass(), conifer.getFieldName(), old.getFieldName(), conifer.getFieldId()));
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException(format("{} 类 解析异常", holder.getEntityClass()), e);
            }
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

    public Object getId() {
        return id;
    }

    public List<FieldConfig> getFieldList() {
        return fieldList.fieldList;
    }

    @Override
    public int compareTo(DTOConfig other) {
        int value = this.packageName.compareTo(other.packageName);
        if (value == 0) {
            return this.className.compareTo(other.className);
        }
        return value;
    }

    @Override
    public String toString() {
        return "DTOConfiger{" +
                "className='" + className + '\'' +
                ", des='" + des + '\'' +
                '}';
    }

}

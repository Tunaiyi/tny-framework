package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;

@XStreamAlias("dto")
public class DTOConfiger implements Comparable<DTOConfiger> {

    private static Map<Integer, DTOConfiger> configerMap = new ConcurrentHashMap<>();

    @XStreamAsAttribute
    private String superClassName;

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

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

    public static DTOConfiger create(DTODocHolder holder, TypeFormatter typeFormatter) {
        int id = holder.getId();
        if (id > 0) {
            DTOConfiger configer = configerMap.get(holder.getId());
            DTOConfiger old;
            if (configer != null) {
                if (configer.getClassName().equals(holder.getEntityClass().getSimpleName()))
                    return configer;
                throw new IllegalArgumentException(format("{} 类 与 {} 类 ID 都为 {}", configer.getClassName(), holder.getEntityClass(), holder.getId()));
            } else {
                configer = new DTOConfiger(holder, typeFormatter);
                old = configerMap.putIfAbsent(configer.getId(), configer);
                if (old != null) {
                    throw new IllegalArgumentException(format("{} 类 与 {} 类 ID 都为 {}", configer.getClassName(), holder.getEntityClass(), holder.getId()));
                } else {
                    return configer;
                }
            }
        }
        throw new IllegalArgumentException(format("{} id 不存在", holder.getEntityClass()));
    }

    private DTOConfiger(DTODocHolder holder, TypeFormatter typeFormatter) {
        super();
        this.className = holder.getEntityClass().getSimpleName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        this.des = holder.getDTODoc().value();
        this.superClassName = holder.getEntityClass().getSuperclass().getSimpleName();
        this.push = holder.getDTODoc().push();
        this.id = holder.getId();
        this.fieldList = new FieldList();
        this.text = holder.getDTODoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
        Map<Integer, FieldConfiger> fieldMap = new HashMap<>();
        List<FieldConfiger> fieldList = new ArrayList<>();
        for (FieldDocHolder fieldDocHolder : holder.getFieldList()) {
            try {
                FieldConfiger configer = new FieldConfiger(fieldDocHolder, typeFormatter);
                fieldList.add(configer);
                FieldConfiger old = fieldMap.put(configer.getFieldId(), configer);
                if (old != null) {
                    throw new IllegalArgumentException(format("{} 类 {} 与 {} 字段 ID 都为 {}",
                            holder.getEntityClass(), configer.getFieldName(), old.getFieldName(), configer.getFieldId()));
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException(format("{} 类 解析一次", holder.getEntityClass()));
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

    public int getId() {
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

    @Override
    public String toString() {
        return "DTOConfiger{" +
                "className='" + className + '\'' +
                ", des='" + des + '\'' +
                '}';
    }
}

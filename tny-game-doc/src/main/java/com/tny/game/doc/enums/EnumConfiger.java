package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.LogUtils;
import com.tny.game.doc.holder.EnumDocHolder;
import com.tny.game.doc.holder.FieldDocHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XStreamAlias("enumeration")
public class EnumConfiger {

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String packageName;

    private EnumerList enumerList = new EnumerList();

    @XStreamAlias("enumerList")
    private static class EnumerList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "enumer")
        private List<EnumerConfiger> enumerList = new ArrayList<EnumerConfiger>();

    }

    public EnumConfiger() {
    }

    public void setEnumDocHolder(EnumDocHolder holder) {
        this.className = holder.getEntityClass().getSimpleName();
        this.des = holder.getClassDoc().value();
        this.packageName = holder.getEntityClass().getPackage().getName();

        Map<String, EnumerConfiger> fieldMap = new HashMap<>();
        for (FieldDocHolder fieldDocHolder : holder.getEnumList()) {
            EnumerConfiger configer = this.createEnumerConfiger(fieldDocHolder);
            this.enumerList.enumerList.add(configer);
            EnumerConfiger old = fieldMap.put(configer.getID(), configer);
            if (old != null) {
                throw new IllegalArgumentException(LogUtils.format("{} 类 {} 与 {} 枚举 ID 都为 {}",
                        holder.getEntityClass(), configer.getName(), old.getName(), configer.getID()));
            }
        }
    }

    protected EnumerConfiger createEnumerConfiger(FieldDocHolder fieldDocHolder) {
        return new EnumerConfiger(fieldDocHolder);
    }

    public String getDes() {
        return this.des;
    }

    public String getClassName() {
        return this.className;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public List<EnumerConfiger> getEnumerList() {
        return Collections.unmodifiableList(this.enumerList.enumerList);
    }

}

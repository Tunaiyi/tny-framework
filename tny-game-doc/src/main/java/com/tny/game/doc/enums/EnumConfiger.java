package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.common.utils.Logs;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.EnumDocHolder;
import com.tny.game.doc.holder.FieldDocHolder;
import org.apache.commons.lang3.StringUtils;

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
    private String text;

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

    public void setEnumDocHolder(EnumDocHolder holder, TypeFormatter typeFormatter) {
        this.className = holder.getClassName();
        this.packageName = holder.getEntityClass().getPackage().getName();
        this.des = holder.getClassDoc().value();
        this.text = holder.getClassDoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
        Map<String, EnumerConfiger> fieldMap = new HashMap<>();
        for (FieldDocHolder fieldDocHolder : holder.getEnumList()) {
            EnumerConfiger configer = this.createEnumerConfiger(fieldDocHolder, typeFormatter);
            this.enumerList.enumerList.add(configer);
            EnumerConfiger old = fieldMap.put(configer.getID(), configer);
            if (old != null) {
                throw new IllegalArgumentException(Logs.format("{} 类 {} 与 {} 枚举 ID 都为 {}",
                        holder.getEntityClass(), configer.getName(), old.getName(), configer.getID()));
            }
        }
    }

    protected EnumerConfiger createEnumerConfiger(FieldDocHolder fieldDocHolder, TypeFormatter typeFormatter) {
        return new EnumerConfiger(fieldDocHolder, typeFormatter);
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

package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.EnumDocHolder;
import com.tny.game.doc.table.TableAttribute;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AllEnumsTableAttribute implements TableAttribute {

    private EnumList enumList = new EnumList();

    private Class<? extends EnumConfiger> enumConfigClass;

    @XStreamAlias("enumerList")
    private static class EnumList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "enum")
        private List<EnumConfiger> enumerList = new ArrayList<>();

    }

    public AllEnumsTableAttribute(Class<? extends EnumConfiger> enumConfigClass) {
        this.enumConfigClass = enumConfigClass;
    }

    @SuppressWarnings("rawtypes")
    public AllEnumsTableAttribute(Class<Enum> clazz, Class<? extends EnumConfiger> enumConfigClass) {
        super();
        try {
            this.enumConfigClass = enumConfigClass;
            EnumConfiger configer = this.enumConfigClass.newInstance();
            configer.setEnumDocHolder(EnumDocHolder.create(clazz));
            this.enumList.enumerList.add(configer);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        try {
            EnumConfiger configer = this.enumConfigClass.newInstance();
            configer.setEnumDocHolder(EnumDocHolder.create((Class<Enum>) clazz));
            this.enumList.enumerList.add(configer);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public EnumList getEnumeration() {
        return this.enumList;
    }

}
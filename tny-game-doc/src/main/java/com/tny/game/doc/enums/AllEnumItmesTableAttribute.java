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
public class AllEnumItmesTableAttribute implements TableAttribute {

    private EnumerList enumerList = new EnumerList();

    private Class<? extends EnumConfiger> enumConfigClass;

    @XStreamAlias("enumerList")
    private static class EnumerList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "enumer")
        private List<EnumerConfiger> enumerList = new ArrayList<EnumerConfiger>();

    }

    public AllEnumItmesTableAttribute(Class<? extends EnumConfiger> enumConfigClass) {
        this.enumConfigClass = enumConfigClass;
    }

    @SuppressWarnings("rawtypes")
    public AllEnumItmesTableAttribute(Class<Enum> clazz, Class<? extends EnumConfiger> enumConfigClass) {
        super();
        try {
            this.enumConfigClass = enumConfigClass;
            EnumConfiger configer = this.enumConfigClass.newInstance();
            configer.setEnumDocHolder(EnumDocHolder.create(clazz));
            this.enumerList.enumerList.addAll(configer.getEnumerList());
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
            this.enumerList.enumerList.addAll(configer.getEnumerList());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public EnumerList getEnumeration() {
        return this.enumerList;
    }

}

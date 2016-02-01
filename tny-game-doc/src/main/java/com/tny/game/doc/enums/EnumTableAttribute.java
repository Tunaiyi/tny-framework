package com.tny.game.doc.enums;

import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.EnumDocHolder;
import com.tny.game.doc.table.TableAttribute;

public class EnumTableAttribute implements TableAttribute {

    private EnumConfiger enumeration;

    private Class<? extends EnumConfiger> enumConfigerClass;

    public EnumTableAttribute(Class<? extends EnumConfiger> enumConfigerClass) {
        super();
        this.enumConfigerClass = enumConfigerClass;
    }

    @SuppressWarnings("rawtypes")
    public EnumTableAttribute(Class<Enum> clazz, Class<? extends EnumConfiger> enumConfigerClass) {
        super();
        try {
            this.enumConfigerClass = enumConfigerClass;
            this.enumeration = this.enumConfigerClass.newInstance();
            this.enumeration.setEnumDocHolder(EnumDocHolder.create(clazz));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        try {
            this.enumeration = this.enumConfigerClass.newInstance();
            this.enumeration.setEnumDocHolder(EnumDocHolder.create((Class<Enum>) clazz));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public EnumConfiger getEnumeration() {
        return this.enumeration;
    }

}

package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.ExportHolder;
import com.tny.game.doc.table.TableAttribute;

import static com.tny.game.doc.holder.EnumDocHolder.*;

public class EnumTableAttribute implements TableAttribute {

    private EnumConfiger enumeration;

    private Class<? extends EnumConfiger> enumConfigerClass;

    @XStreamOmitField
    private ExportHolder exportHolder;

    public EnumTableAttribute(Class<? extends EnumConfiger> enumConfigerClass) {
        super();
        this.enumConfigerClass = enumConfigerClass;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public EnumTableAttribute(Class<Enum> clazz, Class<? extends EnumConfiger> enumConfigerClass) {
        super();
        try {
            this.enumConfigerClass = enumConfigerClass;
            this.enumeration = this.enumConfigerClass.newInstance();
            this.enumeration.setEnumDocHolder(create(clazz));
            this.exportHolder = ExportHolder.create(clazz);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        try {
            this.enumeration = this.enumConfigerClass.newInstance();
            this.enumeration.setEnumDocHolder(create((Class<Enum>) clazz));
            this.exportHolder = ExportHolder.create(clazz);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public EnumConfiger getEnumeration() {
        return this.enumeration;
    }

    @Override
    public String getOutput() {
        return this.exportHolder.getOutput();
    }

    @Override
    public Object getContent() {
        return enumeration;
    }

    @Override
    public String getTemplate() {
        return this.exportHolder.getTemplate();
    }
}

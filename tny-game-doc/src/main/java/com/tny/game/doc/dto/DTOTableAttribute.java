package com.tny.game.doc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

public class DTOTableAttribute implements TableAttribute {

    private DTOConfiger dto;

    @JsonIgnore
    @XStreamOmitField
    private ExportHolder exportHolder;

    public DTOTableAttribute() {
        super();
    }

    public DTOTableAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        super();
        this.dto = DTOConfiger.create(DTODocHolder.create(clazz), typeFormatter);
        this.exportHolder = ExportHolder.create(clazz);
    }

    public DTOConfiger getDto() {
        return dto;
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        this.dto = DTOConfiger.create(DTODocHolder.create(clazz), typeFormatter);
        this.exportHolder = ExportHolder.create(clazz);
    }

    @Override
    public String getOutput() {
        return this.exportHolder.getOutput();
    }

    @Override
    public Object getContent() {
        return dto;
    }

    @Override
    public String getTemplate() {
        return this.exportHolder.getTemplate();
    }

    @Override
    public String toString() {
        return "DTOTableAttribute{" +
               "dto=" + dto +
               '}';
    }
}

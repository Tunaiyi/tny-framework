package com.tny.game.doc.dto;

import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.DTODocHolder;
import com.tny.game.doc.table.TableAttribute;

public class DTOTableAttribute implements TableAttribute {

    private DTOConfiger dto;

    public DTOTableAttribute() {
        super();
    }

    public DTOTableAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        super();
        this.dto = new DTOConfiger(DTODocHolder.create(clazz), typeFormatter);
    }

    public DTOConfiger getDto() {
        return dto;
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        this.dto = new DTOConfiger(DTODocHolder.create(clazz), typeFormatter);
    }

}

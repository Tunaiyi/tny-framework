package com.tny.game.doc.label;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.doc.enums.*;
import com.tny.game.doc.holder.*;

@XStreamAlias("enumeration")
public class ItemLabelEnumConfiger extends EnumConfiger {

    @XStreamAsAttribute
    private String[] labelGroup = new String[0];

    @Override
    public void setEnumDocHolder(EnumDocHolder holder, TypeFormatter typeFormatter) {
        ItemLabel label = holder.getEntityClass().getAnnotation(ItemLabel.class);
        this.labelGroup = label.groups();
        super.setEnumDocHolder(holder, typeFormatter);
    }

    @Override
    protected EnumerConfiger createEnumerConfiger(FieldDocHolder fieldDocHolder, TypeFormatter typeFormatter) {
        return new ItemLabelEnumerConfiger(fieldDocHolder, typeFormatter, this.labelGroup);
    }

}

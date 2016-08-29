package com.tny.game.doc.label;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.doc.annotation.ItemLabel;
import com.tny.game.doc.enums.EnumConfiger;
import com.tny.game.doc.enums.EnumerConfiger;
import com.tny.game.doc.holder.EnumDocHolder;
import com.tny.game.doc.holder.FieldDocHolder;

@XStreamAlias("enumeration")
public class ItemLabelEnumConfiger extends EnumConfiger {

    @XStreamAsAttribute
    private String[] labelGroup = new String[0];

    @Override
    public void setEnumDocHolder(EnumDocHolder holder) {
        ItemLabel label = holder.getEntityClass().getAnnotation(ItemLabel.class);
        this.labelGroup = label.groups();
        super.setEnumDocHolder(holder);
    }

    @Override
    protected EnumerConfiger createEnumerConfiger(FieldDocHolder fieldDocHolder) {
        return new ItemLabelEnumerConfiger(fieldDocHolder, this.labelGroup);
    }

}

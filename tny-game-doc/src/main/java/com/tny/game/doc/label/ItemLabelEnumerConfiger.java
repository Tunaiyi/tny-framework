package com.tny.game.doc.label;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.doc.enums.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

@XStreamAlias("enumer")
public class ItemLabelEnumerConfiger extends EnumerConfiger {

    @XStreamAsAttribute
    private String labelGroup = null;

    public ItemLabelEnumerConfiger(FieldDocHolder holder, TypeFormatter typeFormatter, String[] labelGroups) {
        super(holder, typeFormatter);
        ItemLabel label = holder.getField().getAnnotation(ItemLabel.class);
        if (label == null) {
            this.labelGroup = StringUtils.join(labelGroups, ",");
        } else {
            this.labelGroup = StringUtils.join(label.groups(), ",");
        }
    }

    public String getLabelGroup() {
        return this.labelGroup;
    }

}

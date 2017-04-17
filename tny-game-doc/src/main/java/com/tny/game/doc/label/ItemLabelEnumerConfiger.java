package com.tny.game.doc.label;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.annotation.ItemLabel;
import com.tny.game.doc.enums.EnumerConfiger;
import com.tny.game.doc.holder.FieldDocHolder;
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

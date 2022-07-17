package com.tny.game.doc.enumeration;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class EnumDescription extends ClassDescription {

    private String des;

    private List<EnumItemDescription> enumItemList;

    public EnumDescription() {
    }

    public EnumDescription(EnumDocHolder holder, TypeFormatter typeFormatter) {
        this.initEnumDescription(holder, typeFormatter);
    }

    public void initEnumDescription(EnumDocHolder holder, TypeFormatter typeFormatter) {
        this.setDocClass(holder);
        this.des = holder.getClassDoc().value();
        Map<String, EnumItemDescription> fieldMap = new HashMap<>();
        List<EnumItemDescription> enumItemList = new ArrayList<>();
        for (DocVar fieldDocHolder : holder.getEnumList()) {
            EnumItemDescription description = new EnumItemDescription(fieldDocHolder, typeFormatter);
            enumItemList.add(description);
            EnumItemDescription old = fieldMap.put(description.getId(), description);
            if (old != null) {
                throw new IllegalArgumentException(format("{} 类 {} 与 {} 枚举 ID 都为 {}",
                        holder.getEntityClass(), description.getName(), old.getName(), description.getId()));
            }
        }
        this.enumItemList = Collections.unmodifiableList(enumItemList);
    }

    public String getDes() {
        return this.des;
    }

    public List<EnumItemDescription> getEnumItemList() {
        return this.enumItemList;
    }

}

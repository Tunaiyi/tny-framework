package com.tny.game.doc.enumeration;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class EnumDescription extends ClassDescription {

    private List<EnumItemDescription> enumItemList;

    public EnumDescription() {
    }

    public EnumDescription(EnumDocClass docClass, TypeFormatter typeFormatter) {
        this.initEnumDescription(docClass, typeFormatter);
    }

    public void initEnumDescription(EnumDocClass docClass, TypeFormatter typeFormatter) {
        this.setDocClass(docClass);
        Map<String, EnumItemDescription> fieldMap = new HashMap<>();
        List<EnumItemDescription> enumItemList = new ArrayList<>();
        for (DocField fieldDocHolder : docClass.getEnumList()) {
            EnumItemDescription description = new EnumItemDescription(fieldDocHolder, typeFormatter);
            enumItemList.add(description);
            EnumItemDescription old = fieldMap.put(description.getId(), description);
            if (old != null) {
                throw new IllegalArgumentException(format("{} 类 {} 与 {} 枚举 ID 都为 {}",
                        docClass.getRawClassName(), description.getName(), old.getName(), description.getId()));
            }
        }
        this.enumItemList = Collections.unmodifiableList(enumItemList);
    }

    public List<EnumItemDescription> getEnumItemList() {
        return this.enumItemList;
    }

}

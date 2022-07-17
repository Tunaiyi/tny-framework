package com.tny.game.doc.enumeration;

import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class JoinedEnumItemsTableAttribute implements TableAttribute {

    private final List<EnumItemDescription> enumItemList = new ArrayList<>();

    public JoinedEnumItemsTableAttribute() {
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
        EnumDescription description = new EnumDescription();
        description.initEnumDescription(EnumDocHolder.create((Class<Enum>)clazz), typeFormatter);
        this.enumItemList.addAll(description.getEnumItemList());
    }

    @Override
    public Map<String, Object> getContext() {
        return MapBuilder.<String, Object>newBuilder()
                .put("enumItemList", enumItemList)
                .build();
    }

    public List<EnumItemDescription> getEnumItemList() {
        return Collections.unmodifiableList(enumItemList);
    }

}

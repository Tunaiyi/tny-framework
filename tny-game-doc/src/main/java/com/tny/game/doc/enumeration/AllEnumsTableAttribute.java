package com.tny.game.doc.enumeration;

import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class AllEnumsTableAttribute implements TableAttribute {

    private final List<EnumDescription> enumerationList = new ArrayList<>();

    public AllEnumsTableAttribute() {
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
        EnumDescription description = new EnumDescription();
        description.initEnumDescription(EnumDocClass.createEnumClass((Class<Enum>)clazz), typeFormatter);
        this.enumerationList.add(description);
    }

    @Override
    public Map<String, Object> getContext() {
        return MapBuilder.<String, Object>newBuilder()
                .put("enumerationList", enumerationList)
                .build();
    }

    public List<EnumDescription> getEnumerationList() {
        return Collections.unmodifiableList(enumerationList);
    }

}

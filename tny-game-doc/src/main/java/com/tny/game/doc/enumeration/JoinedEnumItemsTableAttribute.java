/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
        description.initEnumDescription(EnumDocClass.createEnumClass((Class<Enum>) clazz), typeFormatter);
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

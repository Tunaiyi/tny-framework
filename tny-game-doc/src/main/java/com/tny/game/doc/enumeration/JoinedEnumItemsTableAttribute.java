/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

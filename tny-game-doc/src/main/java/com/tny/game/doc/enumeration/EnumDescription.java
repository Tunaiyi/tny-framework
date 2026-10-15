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
            EnumItemDescription description = new EnumItemDescription(docClass.getRawClass(), fieldDocHolder, typeFormatter);
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

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

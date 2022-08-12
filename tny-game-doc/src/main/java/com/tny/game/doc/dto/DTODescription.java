/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.dto;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class DTODescription extends ClassDescription {

    private final Object id;

    private final boolean push;

    private final List<DTOFieldDescription> dtoFieldList;

    public DTODescription(DTODocClass docClass, TypeFormatter typeFormatter) {
        super(docClass);
        this.id = docClass.getId();
        this.push = docClass.getDTODoc().push();
        Map<Object, DTOFieldDescription> fieldMap = new HashMap<>();
        List<DTOFieldDescription> fieldList = new ArrayList<>();
        for (DocField fieldDocHolder : docClass.getFieldList()) {
            try {
                DTOFieldDescription fieldDescription = new DTOFieldDescription(fieldDocHolder, typeFormatter);
                fieldList.add(fieldDescription);
                DTOFieldDescription old = fieldMap.put(fieldDescription.getFieldId(), fieldDescription);
                if (old != null) {
                    throw new IllegalArgumentException(format("{} 类 {} 与 {} 字段 ID 都为 {}",
                            docClass.getEntityClass(), fieldDescription.getName(), old.getName(), fieldDescription.getFieldId()));
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException(format("{} 类 解析异常", docClass.getEntityClass()), e);
            }
        }
        this.dtoFieldList = Collections.unmodifiableList(fieldList);
    }

    public boolean isPush() {
        return push;
    }

    public Object getId() {
        return id;
    }

    public List<DTOFieldDescription> getDtoFieldList() {
        return dtoFieldList;
    }

    @Override
    public String toString() {
        return "DTODescription{" +
                "className='" + getRawClassName() + '\'' +
                ", desc='" + getDocDesc() + '\'' +
                '}';
    }

}

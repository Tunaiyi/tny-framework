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

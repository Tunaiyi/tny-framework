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

package com.tny.game.doc.dto;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import static com.tny.game.common.utils.StringAide.*;

public class DTOFieldDescription extends FieldDescription {

    public DTOFieldDescription(DocField field, TypeFormatter typeFormatter) {
        super(field, typeFormatter);
        Object idValue = field.getFieldId();
        if (idValue == null) {
            throw new IllegalArgumentException(
                    format("{} 类 {} 字段ID为 null", field.getField().getDeclaringClass(), field.getName()));
        }
        if (idValue instanceof Integer) {
            int fieldId = (int) idValue;
            if (fieldId <= 0) {
                throw new IllegalArgumentException(
                        format("{} 类 {} 字段ID = {} <= 0", field.getField().getDeclaringClass(), field.getName(), fieldId));
            }
        } else {
            throw new IllegalArgumentException(
                    format("{} 类 {} 字段ID非 int 值", field.getField().getDeclaringClass(), field.getName()));
        }
    }

}

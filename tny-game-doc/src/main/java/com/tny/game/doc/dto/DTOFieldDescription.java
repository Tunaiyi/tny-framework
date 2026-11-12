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

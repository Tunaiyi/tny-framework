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

package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;

@ProtoEx(BasicsProtoIDs.LONG_LIST_DTO)
@DTODoc("通用Long List DTO")
public class LongListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<Long> values = new ArrayList<>();

    public LongListDTO() {
    }

    public static LongListDTO values2DTO(List<Long> values) {
        LongListDTO dto = new LongListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static LongListDTO values2DTO(Long... values) {
        LongListDTO dto = new LongListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<Long> getValues() {
        return values;
    }

    @Override
    public String toString() {
        String text = "";
        for (Object value : values)
            text += value;
        return text;
    }

}

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

package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;

@ProtoEx(BasicsProtoIDs.STRING_LIST_DTO)
@DTODoc("通用String List DTO")
public class StringListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<String> values = new ArrayList<>();

    public StringListDTO() {
    }

    public static StringListDTO values2DTO(List<String> values) {
        StringListDTO dto = new StringListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static StringListDTO values2DTO(String... values) {
        StringListDTO dto = new StringListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<String> getValues() {
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

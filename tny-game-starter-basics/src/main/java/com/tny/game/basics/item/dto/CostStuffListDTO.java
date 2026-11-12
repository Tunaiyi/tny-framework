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

import java.util.List;

/**
 * Created by xiaoqing on 2016/3/7.
 */
@ProtoEx(BasicsProtoIDs.COST_STUFF_LIST_DTO)
@DTODoc("消耗物品列表DTO")
public class CostStuffListDTO {

    @VarDoc("物品列表")
    @ProtoExField(1)
    private List<CostStuffDTO> stuffs;

    public List<CostStuffDTO> getStuffs() {
        return stuffs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (CostStuffDTO dto : stuffs) {
            builder.append(dto);
        }
        return builder.toString();
    }

}

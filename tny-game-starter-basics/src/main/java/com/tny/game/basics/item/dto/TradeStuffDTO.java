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
import java.util.stream.Collectors;

@DTODoc("交易物品DTO")
@ProtoEx(BasicsProtoIDs.TRADE_STUFF_DTO)
public class TradeStuffDTO {

    @ProtoExField(1)
    @VarDoc("物品ID")
    public int modelId;

    @ProtoExField(2)
    @VarDoc("物品数量")
    public long number;

    @ProtoExField(3)
    @VarDoc("更改方式 1: 检测上下限 2: 不检测上下限(可超出) 3: 忽略多出")
    public int alterType;

    public static TradeStuffDTO stuff2DTO(TradeStuff stuff) {
        TradeStuffDTO dto = new TradeStuffDTO();
        dto.modelId = stuff.getModelId();
        dto.number = stuff.getNumber();
        dto.alterType = stuff.getAlterType();
        return dto;
    }

    public static List<TradeStuffDTO> stuffs2DTO(Collection<TradeStuff> stuffList) {
        return stuffList.stream().map(TradeStuffDTO::stuff2DTO).collect(Collectors.toList());
    }

    public int getModelId() {
        return this.modelId;
    }

    public long getNumber() {
        return this.number;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public int getAlterType() {
        return alterType;
    }

    public void setAlterType(int alterType) {
        this.alterType = alterType;
    }

    @Override
    public String toString() {
        return "modelId = " + modelId + " number = " + number + " alterType = " + alterType;
    }

}

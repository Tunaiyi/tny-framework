/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

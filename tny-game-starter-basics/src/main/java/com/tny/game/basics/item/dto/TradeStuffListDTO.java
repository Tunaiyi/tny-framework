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
import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

@DTODoc("交易物品列表DTO")
@ProtoEx(BasicsProtoIDs.TRADE_STUFF_LIST_DTO)
public class TradeStuffListDTO {

    @ProtoExField(1)
    @VarDoc("交易物品列表")
    private List<TradeStuffDTO> stuffList;

    @ProtoExField(2)
    @VarDoc("交易类型")
    private TradeType tradeType;

    public List<TradeStuffDTO> getStuffList() {
        return stuffList;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public static TradeStuffListDTO stuff2DTO(List<TradeStuff> stuffs) {
        TradeStuffListDTO dto = new TradeStuffListDTO();
        dto.stuffList = stuffs.stream().map(stuff -> TradeStuffDTO.stuff2DTO(stuff)).collect(Collectors.toList());
        return dto;
    }

    public static TradeStuffListDTO newEmptyDTO() {
        TradeStuffListDTO dto = new TradeStuffListDTO();
        dto.stuffList = new ArrayList<>();
        return dto;
    }

    public void addAward(TradeStuffDTO award) {
        this.stuffList.add(award);
    }

    public void addAward(Collection<TradeStuffDTO> awards) {
        this.stuffList.addAll(awards);
    }

}

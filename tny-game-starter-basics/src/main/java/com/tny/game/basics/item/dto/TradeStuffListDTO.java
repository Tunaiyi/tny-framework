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

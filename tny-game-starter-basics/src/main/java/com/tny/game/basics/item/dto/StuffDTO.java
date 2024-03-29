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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.io.Serializable;
import java.util.*;

@ProtoEx(BasicsProtoIDs.STUFF_DTO)
@DTODoc("物品DTO")
public class StuffDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("条件相关的modelId")
    @ProtoExField(1)
    @JsonProperty
    private int modelId;

    @VarDoc("条件相关的数量")
    @ProtoExField(3)
    @JsonProperty
    private long number;

    private void alterNumber(long alterNum) {
        this.number += alterNum;
    }

    public static StuffDTO tradeItem2DTO(TradeItem<?> item) {
        return dealedItem2DTO(item);
    }

    public static void mergeAward(Map<Integer, StuffDTO> awardMap, TradeItem<?> tradItem) {
        if (tradItem.getNumber().longValue() >= 0) {
            StuffDTO award = awardMap.get(tradItem.getItemModel().getId());
            if (award == null) {
                award = tradeItem2DTO(tradItem);
                awardMap.put(award.modelId, award);
            } else {
                award.alterNumber(tradItem.getNumber().longValue());
            }
        }
    }

    public static void mergeAward(Map<Integer, StuffDTO> awardMap, Trade trade) {
        if (trade.getTradeType() != TradeType.AWARD) {
            return;
        }
        for (TradeItem<?> tradItem : trade.getAllTradeItems()) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static void mergeAward(Map<Integer, StuffDTO> awardMap, Collection<TradeItem<StuffModel>> tradItems) {
        for (TradeItem<?> tradItem : tradItems) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static StuffDTO dealedItem2DTO(DealItem<?> dealedItem) {
        StuffDTO dto = new StuffDTO();
        dto.modelId = dealedItem.getItemModel().getId();
        dto.number = dealedItem.getNumber().longValue();
        return dto;
    }

    public static StuffDTO attr2DTO(int modelId, long number) {
        StuffDTO dto = new StuffDTO();
        dto.modelId = modelId;
        dto.number = number;
        return dto;
    }

    public int getModelId() {
        return modelId;
    }

    public long getNumber() {
        return number;
    }

}

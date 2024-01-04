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

@ProtoEx(BasicsProtoIDs.AWARD_DTO)
@DTODoc("奖励DTO")
public class AwardDTO implements Serializable {

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

    @VarDoc("是否是有效(抽中)的奖励")
    @ProtoExField(4)
    @JsonProperty
    private boolean valid;

    public static AwardDTO tradeItem2DTO(DealItem<?> item) {
        return dealItem2DTO(item);
    }

    public static AwardDTO tradeItem2DTO(TradeItem<?> item) {
        AwardDTO dto = dealItem2DTO(item);
        dto.valid = item.isValid();
        return dto;
    }

    public static void mergeAward(Map<Integer, AwardDTO> awardMap, TradeItem<?> tradItem) {
        if (tradItem.isValid() && tradItem.getNumber().longValue() >= 0) {
            AwardDTO award = awardMap.get(tradItem.getItemModel().getId());
            if (award == null) {
                award = tradeItem2DTO(tradItem);
                awardMap.put(award.modelId, award);
            } else {
                award.alterNumber(tradItem.getNumber().longValue());
            }
        }
    }

    public static void mergeAward(Map<Integer, AwardDTO> awardMap, Trade trade) {
        if (trade.getTradeType() != TradeType.AWARD) {
            return;
        }
        for (TradeItem<?> tradItem : trade.getAllTradeItems()) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static void mergeAward(Map<Integer, AwardDTO> awardMap, Collection<TradeItem<StuffModel>> tradItems) {
        for (TradeItem<?> tradItem : tradItems) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static AwardDTO dealItem2DTO(DealItem<?> dealItem) {
        AwardDTO dto = new AwardDTO();
        dto.modelId = dealItem.getItemModel().getId();
        dto.number = dealItem.getNumber().longValue();
        dto.valid = true;
        return dto;
    }

    public static AwardDTO attr2DTO(int modelId, ItemType type, int number) {
        return attr2DTO(modelId, type, number, true);
    }

    public static AwardDTO attr2DTO(int modelId, ItemType type, int number, boolean valid) {
        AwardDTO dto = new AwardDTO();
        dto.modelId = modelId;
        dto.number = number;
        dto.valid = valid;
        return dto;
    }

    private void alterNumber(long alterNum) {
        this.number += alterNum;
    }

    public int getModelId() {
        return modelId;
    }

    public long getNumber() {
        return number;
    }

}

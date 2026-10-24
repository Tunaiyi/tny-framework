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

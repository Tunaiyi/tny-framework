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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.io.Serializable;
import java.util.*;

@ProtoEx(BasicsProtoIDs.STUFF_LIST_DTO)
@DTODoc("物品列表DTO")
public class StuffListDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("奖励DTO")
    @ProtoExField(1)
    @JsonProperty
    private List<StuffDTO> stuffs;

    public boolean isEmpty() {
        return stuffs == null || stuffs.isEmpty();
    }

    public static StuffListDTO dealResult2DTO(DealResult dealResult) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (DealItem<?> dealedItem : dealResult.getDealItemList()) {
            if (dealedItem.getNumber().longValue() > 0) {
                stuffList.add(StuffDTO.dealedItem2DTO(dealedItem));
            }
        }
        dto.stuffs = stuffList;
        return dto;
    }

    @SafeVarargs
    public static StuffListDTO dealItems2DTO(List<DealItem<?>>... itemLists) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (List<DealItem<?>> list : itemLists) {
            for (DealItem<?> item : list) {
                if (item.getNumber().longValue() > 0) {
                    stuffList.add(StuffDTO.dealedItem2DTO(item));
                }
            }
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO awardDetail2DTO(AwardDetail costDetail) {
        if (costDetail == null) {
            return null;
        }
        return tradeItemList2DTO(costDetail.getAllTradeItemList());
    }

    public static StuffListDTO trade2DTO(TradeInfo trade) {
        if (trade == null) {
            return null;
        }
        return tradeItemList2DTO(trade.getAllTradeItems());
    }

    public static StuffListDTO awardDetails2DTO(List<AwardDetail> costDetail) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (AwardDetail detail : costDetail) {
            for (TradeItem<?> entry : detail.getAllTradeItemList())
                stuffList.add(StuffDTO.tradeItem2DTO(entry));
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO tradeItemList2DTO(Collection<TradeItem<StuffModel>> tradeItemList) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (TradeItem<?> entry : tradeItemList) {
            if (entry.getNumber().longValue() > 0) {
                stuffList.add(StuffDTO.tradeItem2DTO(entry));
            }
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO tradeInfos2DTO(List<? extends TradeInfo> tradeList) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (TradeInfo trade : tradeList) {
            for (TradeItem<StuffModel> entry : trade.getAllTradeItems())
                stuffList.add(StuffDTO.tradeItem2DTO(entry));
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO tradeInfos2DTO(TradeInfo... trades) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (TradeInfo trade : trades) {
            if (trade == null) {
                continue;
            }
            for (TradeItem<StuffModel> entry : trade.getAllTradeItems())
                stuffList.add(StuffDTO.tradeItem2DTO(entry));
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO stuffList2DTO(Trade countTradeAward) {
        if (countTradeAward == null) {
            return null;
        }
        return tradeItemList2DTO(new ArrayList<>(countTradeAward.getAllTradeItems()));
    }

    public static StuffListDTO stuffList2DTO(List<StuffDTO> stuffs) {
        StuffListDTO dto = new StuffListDTO();
        if (stuffs == null) {
            stuffs = new ArrayList<>();
        }
        dto.stuffs = stuffs;
        return dto;
    }

    public List<StuffDTO> getStuffs() {
        return stuffs;
    }

}

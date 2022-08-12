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
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.io.Serializable;
import java.util.*;

@ProtoEx(BasicsProtoIDs.DEMAND_RESULT_DTO)
@DTODoc(value = "判断结果DTO")
public class DemandResultDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("条件相关的modelId")
    @ProtoExField(1)
    protected int modelId;

    @VarDoc("条件相关的item类型")
    @ProtoExField(2)
    protected int itemType;

    @VarDoc("条件类型")
    @ProtoExField(3)
    protected int demandType;

    @VarDoc("期望值")
    @ProtoExField(5)
    protected Long expectValue;

    @VarDoc("条件相关item的Id")
    @ProtoExField(6)
    protected long id;

    //    @VarDoc("期望值")
    //    @ProtoExField(7)
    //    protected Long currentValue;
    //
    //    @VarDoc("期望值")
    //    @ProtoExField(8)
    //    protected boolean satisfy;

    public DemandResultDTO() {
    }

    public static DemandResultDTO demandResult2DTO(DemandResult result) {
        DemandResultDTO dto = new DemandResultDTO();
        setDTO(dto, result);
        return dto;
    }

    protected static void setDTO(DemandResultDTO dto, DemandResult result) {
        dto.id = result.getId();
        dto.modelId = result.getModelId();
        dto.itemType = ItemTypes.ofModelId(result.getModelId()).getId();
        dto.demandType = result.getDemandType().getId();
        dto.expectValue = result.getExpectValue(Long.class);
    }

    public static List<DemandResultDTO> tradeInfo2DTOList(TradeInfo trade) {
        if (trade.getTradeType() == TradeType.AWARD) {
            return Collections.emptyList();
        }
        Collection<TradeItem<StuffModel>> tradeItemList = trade.getAllTradeItems();
        List<DemandResultDTO> list = new ArrayList<>(trade.getAllTradeItems().size());
        for (TradeItem<StuffModel> item : tradeItemList) {
            list.add(deductItemModel2DTO(item.getItemModel(), item.getNumber()));
        }
        return list;
    }

    public static List<DemandResultDTO> deductTrade2DTOList(Trade trade) {
        if (trade.getTradeType() == TradeType.AWARD) {
            return Collections.emptyList();
        }
        Collection<TradeItem<StuffModel>> tradeItemList = trade.getAllTradeItems();
        List<DemandResultDTO> list = new ArrayList<>(trade.getAllTradeItems().size());
        for (TradeItem<StuffModel> item : tradeItemList) {
            list.add(deductItemModel2DTO(item.getItemModel(), item.getNumber()));
        }
        return list;
    }

    public static List<DemandResultDTO> deductTrade2DTOList(Collection<TradeItem<StuffModel>> tradeItemList) {
        List<DemandResultDTO> list = new ArrayList<>(tradeItemList.size());
        for (TradeItem<StuffModel> item : tradeItemList) {
            list.add(deductItemModel2DTO(item.getItemModel(), item.getNumber()));
        }
        return list;
    }

    public static DemandResultDTO deductTradeItem2DTO(TradeItem<StuffModel> tradeItem) {
        DemandResultDTO dto = new DemandResultDTO();
        dto.modelId = tradeItem.getItemModel().getId();
        dto.itemType = tradeItem.getItemModel().getItemType().getId();
        dto.expectValue = tradeItem.getNumber().longValue();
        dto.demandType = TradeDemandType.DEDUCT_DEMAND_GE.getId();
        return dto;
    }

    public static DemandResultDTO deductItemModel2DTO(ItemModel itemModel, Number number) {
        DemandResultDTO dto = new DemandResultDTO();
        dto.modelId = itemModel.getId();
        dto.itemType = itemModel.getItemType().getId();
        dto.expectValue = number.longValue();
        dto.demandType = TradeDemandType.DEDUCT_DEMAND_GE.getId();
        return dto;
    }

    @Override
    public String toString() {
        return "DemandResultDTO [modelId=" + this.modelId + ", demandType="
                + this.demandType + ", expectValue=" + this.expectValue + "]";
    }

}

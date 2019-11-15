package com.tny.game.suite.base.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.io.Serializable;
import java.util.*;

@ProtoEx(SuiteProtoIDs.STUFF_DTO)
@DTODoc("物品DTO")
public class StuffDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("条件相关的itemID")
    @ProtoExField(1)
    @JsonProperty
    private int itemId;

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
                awardMap.put(award.itemId, award);
            } else {
                award.alterNumber(tradItem.getNumber().longValue());
            }
        }
    }

    public static void mergeAward(Map<Integer, StuffDTO> awardMap, Trade trade) {
        if (trade.getTradeType() != TradeType.AWARD)
            return;
        for (TradeItem<?> tradItem : trade.getAllTradeItem()) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static void mergeAward(Map<Integer, StuffDTO> awardMap, Collection<TradeItem<ItemModel>> tradItems) {
        for (TradeItem<?> tradItem : tradItems) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static StuffDTO dealedItem2DTO(DealedItem<?> dealedItem) {
        StuffDTO dto = new StuffDTO();
        dto.itemId = dealedItem.getItemModel().getId();
        dto.number = dealedItem.getNumber().longValue();
        return dto;
    }

    public static StuffDTO attr2DTO(int itemID, long number) {
        StuffDTO dto = new StuffDTO();
        dto.itemId = itemID;
        dto.number = number;
        return dto;
    }

    public int getItemId() {
        return itemId;
    }

    public long getNumber() {
        return number;
    }

}

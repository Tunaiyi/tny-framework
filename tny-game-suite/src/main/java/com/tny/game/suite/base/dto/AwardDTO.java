package com.tny.game.suite.base.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.base.item.DealedItem;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@ProtoEx(SuiteProtoIDs.AWARD_DTO)
@DTODoc("奖励DTO")
public class AwardDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("条件相关的itemID")
    @ProtoExField(1)
    @JsonProperty
    private int itemID;

    // @VarDoc("条件相关的ItemType")
    // @ProtoExField(2)
    // @JsonProperty
    // private int itemType;

    @VarDoc("条件相关的数量")
    @ProtoExField(3)
    @JsonProperty
    private long number;

    @VarDoc("是否是有效(抽中)的奖励")
    @ProtoExField(4)
    @JsonProperty
    private boolean valid;

    public static AwardDTO tradeItem2DTO(DealedItem<?> item) {
        return dealedItem2DTO(item);
    }

    public static AwardDTO tradeItem2DTO(TradeItem<?> item) {
        AwardDTO dto = dealedItem2DTO(item);
        dto.valid = item.isValid();
        return dto;
    }

    public static void mergeAward(Map<Integer, AwardDTO> awardMap, TradeItem<?> tradItem) {
        if (tradItem.isValid() && tradItem.getNumber().longValue() >= 0) {
            AwardDTO award = awardMap.get(tradItem.getItemModel().getID());
            if (award == null) {
                award = tradeItem2DTO(tradItem);
                awardMap.put(award.itemID, award);
            } else {
                award.alterNumber(tradItem.getNumber().longValue());
            }
        }
    }

    public static void mergeAward(Map<Integer, AwardDTO> awardMap, Trade trade) {
        if (trade.getTradeType() != TradeType.AWARD)
            return;
        for (TradeItem<?> tradItem : trade.getAllTradeItem()) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static void mergeAward(Map<Integer, AwardDTO> awardMap, Collection<TradeItem<ItemModel>> tradItems) {
        for (TradeItem<?> tradItem : tradItems) {
            mergeAward(awardMap, tradItem);
        }
    }

    public static AwardDTO dealedItem2DTO(DealedItem<?> dealedItem) {
        AwardDTO dto = new AwardDTO();
        dto.itemID = dealedItem.getItemModel().getID();
        dto.number = dealedItem.getNumber().longValue();
        dto.valid = true;
        return dto;
    }

    public static AwardDTO attr2DTO(int itemID, ItemType type, int number) {
        return attr2DTO(itemID, type, number, true);
    }

    public static AwardDTO attr2DTO(int itemID, ItemType type, int number, boolean valid) {
        AwardDTO dto = new AwardDTO();
        dto.itemID = itemID;
        dto.number = number;
        dto.valid = valid;
        return dto;
    }

    private void alterNumber(long alterNum) {
        this.number += alterNum;
    }

    public int getItemID() {
        return itemID;
    }

    public long getNumber() {
        return number;
    }

}

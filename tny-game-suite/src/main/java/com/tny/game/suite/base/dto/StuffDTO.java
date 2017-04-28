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
    private int itemID;

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
            StuffDTO award = awardMap.get(tradItem.getItemModel().getID());
            if (award == null) {
                award = tradeItem2DTO(tradItem);
                awardMap.put(award.itemID, award);
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
        dto.itemID = dealedItem.getItemModel().getID();
        dto.number = dealedItem.getNumber().longValue();
        return dto;
    }

    public static StuffDTO attr2DTO(int itemID, ItemType type, long number) {
        StuffDTO dto = new StuffDTO();
        dto.itemID = itemID;
        dto.number = number;
        return dto;
    }

    public static StuffDTO attr2DTO(int itemID, int type, long number) {
        StuffDTO dto = new StuffDTO();
        dto.itemID = itemID;
        dto.number = number;
        return dto;
    }

    public int getItemID() {
        return itemID;
    }

    public long getNumber() {
        return number;
    }

}

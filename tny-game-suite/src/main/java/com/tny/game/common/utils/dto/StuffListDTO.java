package com.tny.game.common.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.base.item.DealedItem;
import com.tny.game.base.item.DealedResult;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeInfo;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.AwardDetail;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ProtoEx(SuiteProtoIDs.STUFF_LIST_DTO)
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

    public static StuffListDTO dealedResult2DTO(DealedResult dealedResult) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (DealedItem<?> dealedItem : dealedResult.getDealedItemList()) {
            if (dealedItem.getNumber().longValue() > 0) {
                stuffList.add(StuffDTO.dealedItem2DTO(dealedItem));
            }
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO dealedItems2DTO(List<DealedItem<?>>... itemLists) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (List<DealedItem<?>> list : itemLists) {
            for (DealedItem<?> item : list) {
                if (item.getNumber().longValue() > 0)
                    stuffList.add(StuffDTO.dealedItem2DTO(item));
            }
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO awardDetail2DTO(AwardDetail costDetail) {
        if (costDetail == null)
            return null;
        return tradeItemList2DTO(costDetail.getAllTradeItemList());
    }

    public static StuffListDTO trade2DTO(TradeInfo trade) {
        if (trade == null)
            return null;
        return tradeItemList2DTO(trade.getAllTradeItem());
    }


    public static StuffListDTO awardDetails2DTO(List<AwardDetail> costDetail) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (AwardDetail detail : costDetail) {
            for (TradeItem<ItemModel> entry : detail.getAllTradeItemList())
                stuffList.add(StuffDTO.tradeItem2DTO(entry));
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO tradeItemList2DTO(Collection<TradeItem<ItemModel>> tradeItemList) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (TradeItem<ItemModel> entry : tradeItemList) {
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
            for (TradeItem<ItemModel> entry : trade.getAllTradeItem())
                stuffList.add(StuffDTO.tradeItem2DTO(entry));
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO tradeInfos2DTO(TradeInfo... trades) {
        StuffListDTO dto = new StuffListDTO();
        List<StuffDTO> stuffList = new ArrayList<>();
        for (TradeInfo trade : trades) {
            if (trade == null)
                continue;
            for (TradeItem<ItemModel> entry : trade.getAllTradeItem())
                stuffList.add(StuffDTO.tradeItem2DTO(entry));
        }
        dto.stuffs = stuffList;
        return dto;
    }

    public static StuffListDTO stuffList2DTO(Trade countTradeAward) {
        if (countTradeAward == null)
            return null;
        Collection<TradeItem<ItemModel>> tradeItemList = new ArrayList<>();
        tradeItemList.addAll(countTradeAward.getAllTradeItem());
        return tradeItemList2DTO(tradeItemList);
    }

    public static StuffListDTO stuffList2DTO(List<StuffDTO> stuffs) {
        StuffListDTO dto = new StuffListDTO();
        if (stuffs == null)
            stuffs = new ArrayList<>();
        dto.stuffs = stuffs;
        return dto;
    }

    public List<StuffDTO> getStuffs() {
        return stuffs;
    }

}

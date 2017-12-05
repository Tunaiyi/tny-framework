package com.tny.game.common.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.tny.game.base.item.DealedItem;
import com.tny.game.base.item.DealedResult;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeInfo;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.AwardDetail;
import com.tny.game.base.item.behavior.AwardList;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ProtoEx(SuiteProtoIDs.AWARD_LIST_DTO)
@DTODoc(value = "奖励列表DTO")
public class AwardListDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("奖励DTO")
    @ProtoExField(1)
    @JsonProperty
    private List<AwardDTO> awardList;

    public boolean isEmpty() {
        return awardList == null || awardList.isEmpty();
    }

    public static AwardListDTO dealedResult2DTO(DealedResult dealedResult) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (DealedItem<?> dealedItem : dealedResult.getDealedItemList()) {
            if (dealedItem.getNumber().longValue() > 0) {
                awardList.add(AwardDTO.dealedItem2DTO(dealedItem));
            }
        }
        dto.awardList = awardList;
        return dto;
    }

    public static AwardListDTO awardDetail2DTO(AwardDetail awardDetail) {
        return tradeItemList2DTO(awardDetail.getAllTradeItemList());
    }

    public static AwardListDTO trade2DTO(TradeInfo awardTrade) {
        return tradeItemList2DTO(awardTrade.getAllTradeItem());
    }

    public static AwardListDTO trade2DTO(List<AwardDetail> awardDetail) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (AwardDetail detail : awardDetail) {
            for (TradeItem<ItemModel> entry : detail.getAllTradeItemList())
                awardList.add(AwardDTO.tradeItem2DTO(entry));
        }
        dto.awardList = awardList;
        return dto;
    }

    public static AwardListDTO tradeItemList2DTO(Collection<? extends TradeItem<?>> tradeItemList) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (TradeItem<?> entry : tradeItemList) {
            if (entry.getNumber().longValue() > 0) {
                awardList.add(AwardDTO.tradeItem2DTO(entry));
            }
        }
        dto.awardList = awardList;
        return dto;
    }

    public static AwardListDTO dealedItemList2DTO(Collection<? extends DealedItem<?>> tradeItemList) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (DealedItem<?> entry : tradeItemList) {
            if (entry.getNumber().longValue() > 0) {
                awardList.add(AwardDTO.tradeItem2DTO(entry));
            }
        }
        dto.awardList = awardList;
        return dto;
    }

    public static AwardListDTO award2DTO(List<? extends TradeInfo> tradeList) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (TradeInfo trade : tradeList) {
            for (TradeItem<ItemModel> entry : trade.getAllTradeItem())
                awardList.add(AwardDTO.tradeItem2DTO(entry));
        }
        dto.awardList = awardList;
        return dto;
    }

    public static AwardListDTO awardList2DTO(AwardList awardList) {
        List<AwardDetail> detailsList = awardList.getAwardDetailList();
        Collection<TradeItem<ItemModel>> tradeItemList = new ArrayList<>();
        for (AwardDetail detail : detailsList) {
            tradeItemList.addAll(detail.getAllTradeItemList());
        }
        return tradeItemList2DTO(tradeItemList);
    }

    public static AwardListDTO dealedItems2DTO(List<DealedItem<?>> awardTrade) {
        return dealedItemList2DTO(awardTrade);
    }

    public static AwardListDTO awardList2DTO(Trade countTradeAward) {
        Collection<TradeItem<ItemModel>> tradeItemList = new ArrayList<>();
        tradeItemList.addAll(countTradeAward.getAllTradeItem());
        return tradeItemList2DTO(tradeItemList);
    }

    public static AwardListDTO awardList2DTO(List<AwardDTO> awardDTOList) {
        AwardListDTO dto = new AwardListDTO();
        dto.awardList = awardDTOList;
        return dto;
    }

    public List<AwardDTO> getAwardList() {
        if (awardList == null)
            return ImmutableList.of();
        return awardList;
    }

}

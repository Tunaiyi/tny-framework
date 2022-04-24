package com.tny.game.basics.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.io.Serializable;
import java.util.*;

@ProtoEx(BasicsProtoIDs.AWARD_LIST_DTO)
@DTODoc(value = "奖励列表DTO")
public class AwardListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @VarDoc("奖励DTO")
    @ProtoExField(1)
    @JsonProperty
    private List<AwardDTO> awardList;

    public boolean isEmpty() {
        return awardList == null || awardList.isEmpty();
    }

    public static AwardListDTO dealResult2DTO(DealResult dealResult) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (DealItem<?> dealedItem : dealResult.getDealItemList()) {
            if (dealedItem.getNumber().longValue() > 0) {
                awardList.add(AwardDTO.dealItem2DTO(dealedItem));
            }
        }
        dto.awardList = awardList;
        return dto;
    }

    public static AwardListDTO awardDetail2DTO(AwardDetail awardDetail) {
        return tradeItemList2DTO(awardDetail.getAllTradeItemList());
    }

    public static AwardListDTO trade2DTO(TradeInfo awardTrade) {
        return tradeItemList2DTO(awardTrade.getAllTradeItems());
    }

    public static AwardListDTO trade2DTO(List<AwardDetail> awardDetail) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (AwardDetail detail : awardDetail) {
            for (TradeItem<?> entry : detail.getAllTradeItemList())
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

    public static AwardListDTO dealItemList2DTO(Collection<? extends DealItem<?>> tradeItemList) {
        AwardListDTO dto = new AwardListDTO();
        List<AwardDTO> awardList = new ArrayList<>();
        for (DealItem<?> entry : tradeItemList) {
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
            for (TradeItem<?> entry : trade.getAllTradeItems())
                awardList.add(AwardDTO.tradeItem2DTO(entry));
        }
        dto.awardList = awardList;
        return dto;
    }

    public static AwardListDTO awardList2DTO(AwardList awardList) {
        List<AwardDetail> detailsList = awardList.getAwardDetailList();
        Collection<TradeItem<?>> tradeItemList = new ArrayList<>();
        for (AwardDetail detail : detailsList) {
            tradeItemList.addAll(detail.getAllTradeItemList());
        }
        return tradeItemList2DTO(tradeItemList);
    }

    public static AwardListDTO dealItems2DTO(List<DealItem<?>> awardTrade) {
        return dealItemList2DTO(awardTrade);
    }

    public static AwardListDTO awardList2DTO(Trade countTradeAward) {
        Collection<TradeItem<?>> tradeItemList = new ArrayList<>();
        tradeItemList.addAll(countTradeAward.getAllTradeItems());
        return tradeItemList2DTO(tradeItemList);
    }

    public static AwardListDTO awardList2DTO(List<AwardDTO> awardDTOList) {
        AwardListDTO dto = new AwardListDTO();
        dto.awardList = awardDTOList;
        return dto;
    }

    public List<AwardDTO> getAwardList() {
        if (awardList == null) {
            return ImmutableList.of();
        }
        return awardList;
    }

}

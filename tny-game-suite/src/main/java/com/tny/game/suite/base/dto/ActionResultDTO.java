package com.tny.game.suite.base.dto;

import com.tny.game.base.item.DealedItem;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeInfo;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.ActionResult;
import com.tny.game.base.item.behavior.CostList;
import com.tny.game.base.item.behavior.DemandResult;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.ACTION_RESULT_DTO)
@DTODoc(value = "操作结果DTO")
public class ActionResultDTO  implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("操作")
    @ProtoExField(1)
    private int action;

    @VarDoc("判断结果列表")
    @ProtoExField(2)
    private List<DemandResultDTO> demandResults;

    @VarDoc("奖励列表")
    @ProtoExField(3)
    private AwardListDTO awardList;

    public ActionResultDTO() {

    }

    public static ActionResultDTO costTradeItemList2DTO(Collection<TradeItem<ItemModel>> tradeItemList) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.demandResults = tradeItemList.stream()
                .map(tradeItem -> DemandResultDTO.itemModel2DTO(TradeType.COST, tradeItem.getItemModel(), tradeItem.getNumber()))
                .collect(Collectors.toList());
        return dto;
    }

    public static ActionResultDTO actionResult2DTO(ActionResult actionResult) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = actionResult.getAction().getId();
        List<DemandResultDTO> demandResultDTOList = new ArrayList<>();

        for (DemandResult demandResult : actionResult.getDemandResultList()) {
            demandResultDTOList.add(DemandResultDTO.demandResult2DTO(demandResult));
        }
        for (DemandResult demandResult : actionResult.getCostDemandResultList()) {
            demandResultDTOList.add(DemandResultDTO.demandResult2DTO(demandResult));
        }
        dto.demandResults = demandResultDTOList;
        return dto;
    }

    public static ActionResultDTO actionFullResult2DTO(ActionResult actionResult) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = actionResult.getAction().getId();
        List<DemandResultDTO> demandResultDTOList = new ArrayList<>();
        for (DemandResult demandResult : actionResult.getDemandResultList()) {
            demandResultDTOList.add(DemandResultDTO.demandResult2DTO(demandResult));
        }
        for (DemandResult demandResult : actionResult.getCostDemandResultList()) {
            demandResultDTOList.add(DemandResultDTO.demandResult2DTO(demandResult));
        }
        dto.demandResults = demandResultDTOList;
        dto.awardList = AwardListDTO.awardList2DTO(actionResult.getAwardList());
        return dto;
    }

    public static ActionResultDTO trade2DTO(Trade trade) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = trade.getAction().getId();
        dto.demandResults = new ArrayList<>();
        dto.demandResults.addAll(DemandResultDTO.trade2DTOList(trade));
        return dto;
    }

    public static ActionResultDTO tradeInfo2DTO(TradeInfo trade) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = trade.getAction().getId();
        dto.demandResults = new ArrayList<>();
        dto.demandResults.addAll(DemandResultDTO.tradeInfo2DTOList(trade));
        return dto;
    }

    public static ActionResultDTO costList2DTO(CostList costList) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = costList.getAction().getId();
        dto.demandResults = new ArrayList<>();
        dto.demandResults.addAll(DemandResultDTO.trade2DTOList(TradeType.COST, costList.getAwardTradeItemList()));
        return dto;
    }

    public static ActionResultDTO dealedItem2DTO(Action action, List<DealedItem<?>> list) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = action.getId();
        dto.demandResults = new ArrayList<>();
        List<DemandResultDTO> demandResultDTOList = new ArrayList<>();
        for (DealedItem item : list)
            demandResultDTOList.add(DemandResultDTO.itemModel2DTO(TradeType.COST, item.getItemModel(), item.getNumber()));
        dto.demandResults.addAll(demandResultDTOList);
        return dto;
    }

    @Override
    public String toString() {
        return "Action : " + action + " DemandResultListDTO [demandResultList=" + demandResults + "]";
    }

}

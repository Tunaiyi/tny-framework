/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ProtoEx(BasicsProtoIDs.ACTION_RESULT_DTO)
@DTODoc(value = "操作结果DTO")
public class ActionResultDTO implements Serializable {

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
        dto.demandResults.addAll(DemandResultDTO.deductTrade2DTOList(trade));
        return dto;
    }

    public static ActionResultDTO tradeInfo2DTO(TradeInfo trade) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = trade.getAction().getId();
        dto.demandResults = new ArrayList<>();
        dto.demandResults.addAll(DemandResultDTO.tradeInfo2DTOList(trade));
        return dto;
    }

    public static ActionResultDTO deductTradeItemList2DTO(Collection<TradeItem<StuffModel>> tradeItemList) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.demandResults = tradeItemList.stream()
                .map(tradeItem -> DemandResultDTO
                        .deductItemModel2DTO(tradeItem.getItemModel(), tradeItem.getNumber()))
                .collect(Collectors.toList());
        return dto;
    }

    public static ActionResultDTO deductList2DTO(CostList costList) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = costList.getAction().getId();
        dto.demandResults = new ArrayList<>();
        dto.demandResults.addAll(DemandResultDTO.deductTrade2DTOList(costList.getAwardTradeItemList()));
        return dto;
    }

    public static ActionResultDTO dealItem2DTO(Action action, List<DealItem<?>> list) {
        ActionResultDTO dto = new ActionResultDTO();
        dto.action = action.getId();
        dto.demandResults = new ArrayList<>();
        List<DemandResultDTO> demandResultDTOList = new ArrayList<>();
        for (DealItem<?> item : list) {
            demandResultDTOList.add(DemandResultDTO.deductItemModel2DTO(item.getItemModel(), item.getNumber()));
        }
        dto.demandResults.addAll(demandResultDTOList);
        return dto;
    }

    @Override
    public String toString() {
        return "Action : " + action + " DemandResultListDTO [demandResultList=" + demandResults + "]";
    }

}

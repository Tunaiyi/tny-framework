package com.tny.game.suite.base.dto;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;
import com.tny.game.suite.base.*;

import java.util.*;
import java.util.stream.Collectors;

@DTODoc("交易物品列表DTO")
@ProtoEx(SuiteProtoIDs.TRADE_STUFF_LIST_DTO)
public class TradeStuffListDTO {

    @ProtoExField(1)
    @VarDoc("交易物品列表")
    private List<TradeStuffDTO> stuffList;

    @ProtoExField(2)
    @VarDoc("交易类型")
    private TradeType tradeType;

    public List<TradeStuffDTO> getStuffList() {
        return stuffList;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public static TradeStuffListDTO stuff2DTO(List<TradeStuff> stuffs) {
        TradeStuffListDTO dto = new TradeStuffListDTO();
        dto.stuffList = stuffs.stream().map(stuff -> TradeStuffDTO.stuff2DTO(stuff)).collect(Collectors.toList());
        return dto;
    }

    public static TradeStuffListDTO newEmptyDTO() {
        TradeStuffListDTO dto = new TradeStuffListDTO();
        dto.stuffList = new ArrayList<>();
        return dto;
    }

    public void addAward(TradeStuffDTO award) {
        this.stuffList.add(award);
    }

    public void addAward(Collection<TradeStuffDTO> awards) {
        this.stuffList.addAll(awards);
    }

}

package com.tny.game.suite.base.dto;

import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.TradeStuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
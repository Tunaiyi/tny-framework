package com.tny.game.suite.base.dto;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.TradeStuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xiaoqing on 2016/9/2.
 */
@ProtoEx(SuiteProtoIDs.TRADE_STUFF_LIST_DTO)
public class TradeStuffListDTO {

    @ProtoExField(1)
    private List<TradeStuffDTO> stuffList;

    public List<TradeStuffDTO> getStuffList() {
        return stuffList;
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

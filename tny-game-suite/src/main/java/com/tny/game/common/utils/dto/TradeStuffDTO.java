package com.tny.game.common.utils.dto;


import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.common.utils.TradeStuff;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@DTODoc("交易物品DTO")
@ProtoEx(SuiteProtoIDs.TRADE_STUFF_DTO)
public class TradeStuffDTO {

    @ProtoExField(1)
    @VarDoc("物品ID")
    public int itemID;

    @ProtoExField(2)
    @VarDoc("物品数量")
    public long number;

    @ProtoExField(3)
    @VarDoc("更改方式 1: 检测上下限 2: 不检测上下限(可超出) 3: 忽略多出")
    public int alterType;


    public static TradeStuffDTO stuff2DTO(TradeStuff stuff) {
        TradeStuffDTO dto = new TradeStuffDTO();
        dto.itemID = stuff.getItemID();
        dto.number = stuff.getNumber();
        dto.alterType = stuff.getAlterType();
        return dto;
    }

    public static List<TradeStuffDTO> stuffs2DTO(Collection<TradeStuff> stuffList) {
        return stuffList.stream().map(TradeStuffDTO::stuff2DTO).collect(Collectors.toList());
    }


    public int getItemID() {
        return this.itemID;
    }

    public long getNumber() {
        return this.number;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public int getAlterType() {
        return alterType;
    }

    public void setAlterType(int alterType) {
        this.alterType = alterType;
    }

    @Override
    public String toString() {
        return "itemID = "+itemID+" number = "+number +" alterType = "+alterType;
    }
}

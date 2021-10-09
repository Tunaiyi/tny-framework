package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

@DTODoc("交易物品DTO")
@ProtoEx(BasicsProtoIDs.TRADE_STUFF_DTO)
public class TradeStuffDTO {

	@ProtoExField(1)
	@VarDoc("物品ID")
	public int itemId;

	@ProtoExField(2)
	@VarDoc("物品数量")
	public long number;

	@ProtoExField(3)
	@VarDoc("更改方式 1: 检测上下限 2: 不检测上下限(可超出) 3: 忽略多出")
	public int alterType;

	public static TradeStuffDTO stuff2DTO(TradeStuff stuff) {
		TradeStuffDTO dto = new TradeStuffDTO();
		dto.itemId = stuff.getItemId();
		dto.number = stuff.getNumber();
		dto.alterType = stuff.getAlterType();
		return dto;
	}

	public static List<TradeStuffDTO> stuffs2DTO(Collection<TradeStuff> stuffList) {
		return stuffList.stream().map(TradeStuffDTO::stuff2DTO).collect(Collectors.toList());
	}

	public int getItemId() {
		return this.itemId;
	}

	public long getNumber() {
		return this.number;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
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
		return "itemId = " + itemId + " number = " + number + " alterType = " + alterType;
	}

}

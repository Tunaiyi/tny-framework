package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * Created by xiaoqing on 2016/3/7.
 */
@ProtoEx(BasicsProtoIDs.COST_STUFF_DTO)
@DTODoc("消耗物品DTO")
public class CostStuffDTO {

	@VarDoc("id")
	@ProtoExField(3)
	private long id;

	@VarDoc("itemId")
	@ProtoExField(1)
	private int itemId;

	@VarDoc("数量")
	@ProtoExField(2)
	private int number;

	public int getItemId() {
		return itemId;
	}

	public int getNumber() {
		return number;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(id);
		buffer.append(itemId);
		buffer.append(number);
		return buffer.toString();
	}

}

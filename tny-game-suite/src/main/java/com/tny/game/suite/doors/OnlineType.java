package com.tny.game.suite.doors;

import com.tny.game.doc.annotation.ClassDoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.ProtoExEnum;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.suite.SuiteProtoIDs;


@ClassDoc("在线状态")
@ProtoEx(SuiteProtoIDs.CLUSTER_$ONLINE_STATE)
public enum OnlineType implements ProtoExEnum {

	@VarDoc("空闲")
	IDLE(1),

	@VarDoc("流畅")
	SMOOTH(2),

	@VarDoc("繁忙")
	BUSY(3),

	@VarDoc("爆满")
	FULL(4),

	@VarDoc("推荐")
	NEW(5);

	private final int id;

	private OnlineType(int id) {
		this.id = id;
	}

	@Override
	public int getID() {
		return this.id;
	}

	public static OnlineType get(int typeID) {
		for (OnlineType type : values())
			if (type.id == typeID)
				return type;
		return null;
	}

}

package com.tny.game.suite.doors;

import com.tny.game.doc.annotation.ClassDoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.ProtoExEnum;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.suite.SuiteProtoIDs;


@ClassDoc("入口状态")
@ProtoEx(SuiteProtoIDs.CLUSTER_$ENTRY_STATE)
public enum EntryState implements ProtoExEnum {

	@VarDoc("关闭")
	OFFLINE(0),

	@VarDoc("上线")
	ONLINE(1),

	@VarDoc("维护")
	MAINTAIN(2),

	@VarDoc("测试")
	TEST(3),

	;

	private final int id;

	private EntryState(int id) {
		this.id = id;
	}

	@Override
	public int getID() {
		return this.id;
	}

	public static EntryState get(int stateID) {
		for (EntryState state : values())
			if (state.id == stateID)
				return state;
		return OFFLINE;
	}

}

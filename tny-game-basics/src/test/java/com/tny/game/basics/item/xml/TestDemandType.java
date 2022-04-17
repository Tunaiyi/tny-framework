package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.result.*;

public enum TestDemandType implements DemandType, DemandParam {

	PLAYER_LEVEL,

	PLAYER_ONLINE;

	@Override
	public ResultCode getResultCode() {
		return null;
	}

	@Override
	public boolean isCost() {
		return false;
	}

	@Override
	public int id() {
		return 0;
	}

}

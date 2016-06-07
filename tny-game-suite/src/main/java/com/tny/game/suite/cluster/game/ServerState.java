package com.tny.game.suite.cluster.game;

import com.tny.game.protoex.ProtoExEnum;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.suite.SuiteProtoIDs;

@ProtoEx(SuiteProtoIDs.CLUSTER_$SERVER_STATE)
public enum ServerState implements ProtoExEnum {

	OFFLINE(0),

	ONLINE(1),

	INVALID(2);

	private final int id;

	ServerState(int id) {
		this.id = id;
	}

	@Override
	public int getID() {
		return this.id;
	}

	public static ServerState get(int stateID) {
		for (ServerState state : values())
			if (state.id == stateID)
				return state;
		return null;
	}

}

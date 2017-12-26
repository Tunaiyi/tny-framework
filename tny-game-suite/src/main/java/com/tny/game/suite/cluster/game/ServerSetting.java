package com.tny.game.suite.cluster.game;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

@ProtoEx(SuiteProtoIDs.CLUSTER_$SERVER_SETTING)
public class ServerSetting {

	@ProtoExField(1)
	private int serverID;

	@ProtoExField(2)
	private ServerState serverState;

	@ProtoExField(4)
	private String clientVersion;

	@ProtoExField(5)
	private String name;

	@ProtoExField(6)
	private String url;

	public ServerSetting() {
	}

	public ServerSetting(ServerOutline outline) {
		this.setName("s" + outline.getServerID() + " 服")
				.setServerID(outline.getServerID())
				.setClientVersion("")
				.setServerState(ServerState.OFFLINE);
	}

	public String getName() {
		return this.name;
	}

	public ServerSetting setName(String name) {
		this.name = name;
		return this;
	}

	public int getServerID() {
		return this.serverID;
	}

	protected ServerSetting setServerID(int serverID) {
		this.serverID = serverID;
		return this;
	}

	public ServerState getServerState() {
		return this.serverState;
	}

	public ServerSetting setServerState(ServerState serverState) {
		this.serverState = serverState;
		return this;
	}

	public String getClientVersion() {
		return this.clientVersion;
	}

	public ServerSetting setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.serverID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		ServerSetting other = (ServerSetting) obj;
		if (this.serverID != other.serverID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServerSetting [serverState=" + this.serverState + ", clientVersion=" + this.clientVersion + "]";
	}

}

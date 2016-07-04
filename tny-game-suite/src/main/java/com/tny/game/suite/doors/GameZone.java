package com.tny.game.suite.doors;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ProtoEx(SuiteProtoIDs.CLUSTER_$GAMERS_SERVER_LIST)
public class GameZone {

	@ProtoExField(1)
	private int zoneID;

	@ProtoExField(2)
	private List<GameEntry> entries = new ArrayList<>();

	@ProtoExField(3)
	private String clientVersion = "";

	public GameZone() {
	}

	public GameZone(Zone zone) {
		this.clientVersion = "";
		this.zoneID = zone.getZoneID();
		for (Entry entry : zone.getEntries()) {
			this.entries.add(new GameEntry(entry));
		}
	}

	public int getZoneID() {
		return this.zoneID;
	}

	public String getClientVersion() {
		return this.clientVersion;
	}

	public List<GameEntry> getEntries() {
		return Collections.unmodifiableList(this.entries);
	}

	@Override
	public String toString() {
		return "GamesServerList [clientVersion=" + this.clientVersion + ", serverList=" + this.entries + "]";
	}

}

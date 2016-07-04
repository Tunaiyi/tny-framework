package com.tny.game.suite.doors;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

@ProtoEx(SuiteProtoIDs.CLUSTER_$GAMER_ENTRYSTATE)
public class GameEntry {

	@ProtoExField(1)
	private int serverID;

	@ProtoExField(2)
	private int entryNumber;

	@ProtoExField(3)
	private String enterName;

	@ProtoExField(6)
	private int onlineState;

	@ProtoExField(7)
	private int entryState;

	public GameEntry() {
	}

	public GameEntry(Entry entry) {
		this.serverID = entry.getServer().getServerID();
		this.entryNumber = entry.getNumber();
		this.enterName = entry.getName();
		this.onlineState = entry.getOnlineType().getID();
		this.entryState = entry.getEntryState().getID();
	}

	public int getServerID() {
		return this.serverID;
	}

	public String getServerName() {
		return this.enterName;
	}

	public int getEntryState() {
		return this.entryState;
	}

	public int getEntryNumber() {
		return this.entryNumber;
	}

	public String getEnterName() {
		return this.enterName;
	}

	public int getOnlineState() {
		return this.onlineState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.enterName == null) ? 0 : this.enterName.hashCode());
		result = prime * result + this.entryState;
		result = prime * result + this.onlineState;
		result = prime * result + this.serverID;
		result = prime * result + this.entryNumber;
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
		GameEntry other = (GameEntry) obj;

		if (this.entryState != other.entryState)
			return false;
		if (this.onlineState != other.onlineState)
			return false;
		if (this.serverID != other.serverID)
			return false;
		if (this.entryNumber != other.entryNumber)
			return false;

		if (this.enterName == null) {
			if (other.enterName != null)
				return false;
		} else if (!this.enterName.equals(other.enterName))
			return false;
		return true;
	}

}

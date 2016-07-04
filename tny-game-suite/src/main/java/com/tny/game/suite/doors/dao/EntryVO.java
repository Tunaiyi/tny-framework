package com.tny.game.suite.doors.dao;

public class EntryVO {

	/**
	 * 服务器编号
	 */
	private int number;
	/**
	 * 所属区 
	 */
	private int zoneID;
	/**
	 * 服务器ID
	 */
	private int serverID;
	/**
	 * 入口名称
	 */
	private String name;

	/**
	 * 上线状态
	 */
	private int onlineType;

	/**
	 * 入口状态
	 */
	private int entryState;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getZoneID() {
		return this.zoneID;
	}

	public void setZoneID(int zoneID) {
		this.zoneID = zoneID;
	}

	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getServerID() {
		return this.serverID;
	}

	public void setServerID(int serverID) {
		this.serverID = serverID;
	}

	public int getOnlineType() {
		return this.onlineType;
	}

	public void setOnlineType(int onlineType) {
		this.onlineType = onlineType;
	}

	public int getEntryState() {
		return this.entryState;
	}

	public void setEntryState(int entryState) {
		this.entryState = entryState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.number;
		result = prime * result + this.zoneID;
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
		EntryVO other = (EntryVO) obj;
		if (this.number != other.number)
			return false;
		if (this.zoneID != other.zoneID)
			return false;
		return true;
	}

}

package com.tny.game.suite.doors.dao;

/**
 * 平台数据对象
 * @author KGTny
 *
 */
public class PlatformVO {

	private int id;

	/**
	 * 平台标识
	 */
	private String mark = "";

	/**
	 * 平台名字
	 */
	private String name;

	/**
	 * 所属大区ID
	 */
	private int zoneID;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMark() {
		return this.mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String desc) {
		this.name = desc;
	}

	public int getZoneID() {
		return this.zoneID;
	}

	public void setZoneID(int zoneID) {
		this.zoneID = zoneID;
	}

}

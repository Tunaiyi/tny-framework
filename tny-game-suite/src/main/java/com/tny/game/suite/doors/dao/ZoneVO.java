package com.tny.game.suite.doors.dao;

/**
 * 服务大区
 * @author KGTny
 *
 */
public class ZoneVO {

	/**
	 * 大区号
	 */
	private int zoneID;

	/**
	 * 大区名字
	 */
	private String name;

	/**
	 * 版本号
	 */
	private String version;

	public ZoneVO() {
	}

	public int getZoneID() {
		return this.zoneID;
	}

	public void setZoneID(int zoneID) {
		this.zoneID = zoneID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

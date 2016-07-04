package com.tny.game.suite.doors;

public class Platform {

	private int id;

	private String mark = "";

	private String name;

	private Zone zone;

	public Platform() {
	}

	public Platform(int id, String mark, String name, Zone zone) {
		this.id = id;
		this.mark = mark;
		this.name = name;
		this.zone = zone;
	}

	public int getID() {
		return this.id;
	}

	public void setID(int id) {
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

	public Zone getZone() {
		return this.zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public int getZoneID() {
		Zone zone = this.zone;
		return zone == null ? -1 : zone.getZoneID();
	}

}

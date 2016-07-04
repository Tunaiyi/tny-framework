package com.tny.game.suite.doors;

import com.tny.game.common.utils.collection.CopyOnWriteMap;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Zone {

	/**
	 * 大区号
	 */
	private int zoneID;

	/**
	 * 大区名字
	 */
	private String name;

	/**
	 * 入口列表
	 */
	private Map<Integer, Entry> entriesMap = new CopyOnWriteMap<>();

	/**
	 * 版本号
	 */
	private String version;

	/**
	 * 版本号序列
	 */
	private int[] versionArray;

	public Zone(int zoneID, String name, String version) {
		this.zoneID = zoneID;
		this.name = name;
		this.setVersion(version);
	}

	public int getZoneID() {
		return this.zoneID;
	}

	protected void setZoneID(int zoneID) {
		this.zoneID = zoneID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Entry> getEntries() {
		return new ArrayList<>(this.entriesMap.values());
	}

	public Entry getEntryBy(int number) {
		return this.entriesMap.get(number);
	}

	protected Entry removeEntry(Integer num) {
		return this.entriesMap.remove(num);
	}

	protected void updateEntry(Entry entry) {
		this.entriesMap.put(entry.getNumber(), entry);
		entry.setZoneID(this.zoneID);
	}

	protected void setEntries(Set<Entry> entries) {
		for (Entry entry : entries) {
			this.entriesMap.put(entry.getNumber(), entry);
		}
	}

	public Collection<Entry> getVisibleEntries() {
		Collection<Entry> entries = new ArrayList<>();
		for (Entry entry : this.entriesMap.values()) {
			if (entry.getEntryState() == EntryState.OFFLINE || entry.getEntryState() == EntryState.TEST)
				continue;
			entries.add(entry);
		}
		return entries;
	}

	public Entry getLastEntry() {
		Entry last = null;
		for (Entry entry : this.entriesMap.values()) {
			if (entry.getEntryState() == EntryState.OFFLINE || entry.getEntryState() == EntryState.TEST)
				continue;
			if (last == null || entry.getNumber() > last.getNumber())
				last = entry;
		}
		return last;
	}

	public Entry getTestEntry() {
		Entry last = null;
		for (Entry entry : this.entriesMap.values()) {
			if (entry.getEntryState() != EntryState.TEST)
				continue;
			if (last == null || entry.getNumber() < last.getNumber())
				last = entry;
		}
		return last;
	}

	public List<Entry> getTestEntries() {
		List<Entry> entries = new ArrayList<>();
		for (Entry entry : this.entriesMap.values()) {
			if (entry.getEntryState() == EntryState.TEST)
				entries.add(entry);
		}
		return entries;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		if (StringUtils.isBlank(version) || !VersionUtils.check(version)) {
			this.version = null;
			this.versionArray = new int[0];
		} else {
			this.version = version;
			this.versionArray = VersionUtils.version2Ints(version);
		}
	}

	public int[] getVersionArray() {
		return this.versionArray;
	}

}
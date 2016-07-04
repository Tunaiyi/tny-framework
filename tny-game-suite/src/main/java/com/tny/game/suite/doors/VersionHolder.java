package com.tny.game.suite.doors;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ProtoEx(SuiteProtoIDs.CLUSTER_$VERSION_HOLDER)
public class VersionHolder {

	@ProtoExField(1)
	private List<String> versionList = new CopyOnWriteArrayList<>();

	public List<String> getVersionList() {
		return Collections.unmodifiableList(this.versionList);
	}

	public boolean addVersion(String version) {
		synchronized (this) {
			if (this.versionList.contains(version))
				return false;
			this.versionList.add(version);
			return true;
		}
	}

	public void removeVersion(String... versions) {
		synchronized (this) {
			this.versionList.removeAll(Arrays.asList(versions));
		}
	}

	public void removeVersion(Collection<String> versions) {
		synchronized (this) {
			this.versionList.removeAll(versions);
		}
	}

	public boolean isExistVersion(String version) {
		return this.versionList.contains(version);
	}

}

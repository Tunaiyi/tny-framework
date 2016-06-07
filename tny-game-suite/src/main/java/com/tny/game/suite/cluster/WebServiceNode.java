package com.tny.game.suite.cluster;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;


@ProtoEx(SuiteProtoIDs.CLUSTER_$URL_WEB_SERVICE_NODE)
public class WebServiceNode {

	@ProtoExField(1)
	private int serverID;

	@ProtoExField(2)
	private String url;

	public WebServiceNode() {
	}

	public WebServiceNode(int serverID, String url) {
		this.serverID = serverID;
		this.url = url;
	}

	public int getServerID() {
		return this.serverID;
	}

	public String getUrl() {
		return this.url;
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
		WebServiceNode other = (WebServiceNode) obj;
		if (this.serverID != other.serverID)
			return false;
		return true;
	}

}

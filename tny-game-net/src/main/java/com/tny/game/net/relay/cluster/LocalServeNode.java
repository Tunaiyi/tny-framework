package com.tny.game.net.relay.cluster;

import com.tny.game.common.collection.map.access.*;
import com.tny.game.net.base.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class LocalServeNode implements ServeNode {

	private final String appType;

	private final String scopeType;

	private final long id;

	private final String serveName;

	private final String scheme;

	private final String host;

	private final int port;

	private final ObjectMap metadata = new ObjectMap();

	public LocalServeNode(NetAppContext netAppContext, String serveName, String scheme, String host, int port) {
		this.id = netAppContext.getServerId();
		this.appType = netAppContext.getAppType();
		this.scopeType = netAppContext.getScopeType();
		this.serveName = serveName;
		this.scheme = scheme;
		this.host = host;
		this.port = port;
	}

	@Override
	public String getServeName() {
		return serveName;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getAppType() {
		return appType;
	}

	@Override
	public String getScopeType() {
		return scopeType;
	}

	@Override
	public boolean isHealthy() {
		return true;
	}

	@Override
	public int getPort() {
		return port;
	}

	public LocalServeNode putMetadata(String key, Object value) {
		this.metadata.put(key, value);
		return this;
	}

	public LocalServeNode putMetadata(Map<String, Object> data) {
		this.metadata.putAll(data);
		return this;
	}

	public LocalServeNode removeMetadata(String key) {
		this.metadata.remove(key);
		return this;
	}

	public LocalServeNode removeMetadata(Collection<String> keys) {
		keys.forEach(this.metadata::remove);
		return this;
	}

	@Override
	public ObjectMap getMetadata() {
		return metadata;
	}

	@Override
	public MapAccessor metadata() {
		return metadata;
	}

}

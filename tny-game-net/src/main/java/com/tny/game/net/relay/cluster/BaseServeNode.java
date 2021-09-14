package com.tny.game.net.relay.cluster;

import com.tny.game.common.collection.map.access.*;
import org.apache.commons.lang3.builder.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class BaseServeNode implements ServeNode {

	private String serveName;

	private long id;

	private String appType;

	private String scopeType;

	private String scheme;

	private String host;

	private boolean healthy = true;

	private int port;

	private ObjectMap metadata = new ObjectMap();

	public BaseServeNode() {
	}

	public BaseServeNode(String serveName, String appType, String scopeType, long id, String scheme, String host, int port) {
		this.serveName = serveName;
		this.id = id;
		this.scheme = scheme;
		this.appType = appType;
		this.scopeType = scopeType;
		this.host = host;
		this.port = port;
		this.metadata = new ObjectMap();
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
		return healthy;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public Map<String, Object> getMetadata() {
		return Collections.unmodifiableMap(metadata);
	}

	@Override
	public MapAccessor metadata() {
		return metadata;
	}

	protected BaseServeNode setServeName(String serveName) {
		this.serveName = serveName;
		return this;
	}

	protected BaseServeNode setId(long id) {
		this.id = id;
		return this;
	}

	protected BaseServeNode setAppType(String appType) {
		this.appType = appType;
		return this;
	}

	protected BaseServeNode setScopeType(String scopeType) {
		this.scopeType = scopeType;
		return this;
	}

	protected BaseServeNode setHealthy(boolean healthy) {
		this.healthy = healthy;
		return this;
	}

	protected BaseServeNode setScheme(String scheme) {
		this.scheme = scheme;
		return this;
	}

	protected BaseServeNode setHost(String host) {
		this.host = host;
		return this;
	}

	protected BaseServeNode setPort(int port) {
		this.port = port;
		return this;
	}

	protected BaseServeNode setMetadata(Map<String, Object> metadata) {
		this.metadata = new ObjectMap(metadata);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BaseServeNode)) {
			return false;
		}

		BaseServeNode that = (BaseServeNode)o;

		return new EqualsBuilder().append(getId(), that.getId()).append(getServeName(), that.getServeName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getServeName()).append(getId()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append("serveName", serveName)
				.append("id", id)
				.append("healthy", healthy)
				.append("scheme", scheme)
				.append("host", host)
				.append("port", port)
				.toString();
	}

}

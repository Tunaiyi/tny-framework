package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.common.collection.map.access.*;
import com.tny.game.net.relay.cluster.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class NacosServeNode extends RemoteServeNode {

	public NacosServeNode(Instance instance, ObjectMapper mapper) throws JsonProcessingException {
		Map<String, String> metadata = instance.getMetadata();
		String serveName = metadata.get(NacosMetaDataKey.NET_SERVE_NAME);
		String serverId = metadata.getOrDefault(NacosMetaDataKey.NET_SERVER_ID, "0");
		String netMetadata = metadata.get(NacosMetaDataKey.NET_METADATA);
		this.setServeName(serveName)
				.setId(Long.parseLong(serverId))
				.setAppType(metadata.get(NacosMetaDataKey.NET_APP_TYPE))
				.setScopeType(metadata.get(NacosMetaDataKey.NET_SCOPE_TYPE))
				.setScheme(metadata.get(NacosMetaDataKey.NET_SCHEME))
				.setHost(instance.getIp())
				.setPort(instance.getPort())
				.setHealthy(instance.isHealthy());
		if (StringUtils.isNoneBlank(netMetadata)) {
			this.setMetadata(mapper.readValue(netMetadata, ObjectMap.class));
		}
	}

	@Override
	protected NacosServeNode setServeName(String serveName) {
		super.setServeName(serveName);
		return this;
	}

	@Override
	protected NacosServeNode setId(long id) {
		super.setId(id);
		return this;
	}

	@Override
	protected NacosServeNode setAppType(String appType) {
		super.setAppType(appType);
		return this;
	}

	@Override
	protected NacosServeNode setScopeType(String scopeType) {
		super.setScopeType(scopeType);
		return this;
	}

	@Override
	protected NacosServeNode setHealthy(boolean healthy) {
		super.setHealthy(healthy);
		return this;
	}

	@Override
	protected NacosServeNode setScheme(String scheme) {
		super.setScheme(scheme);
		return this;
	}

	@Override
	protected NacosServeNode setHost(String host) {
		super.setHost(host);
		return this;
	}

	@Override
	protected NacosServeNode setPort(int port) {
		super.setPort(port);
		return this;
	}

	@Override
	protected NacosServeNode setMetadata(Map<String, Object> metadata) {
		super.setMetadata(metadata);
		return this;
	}

}

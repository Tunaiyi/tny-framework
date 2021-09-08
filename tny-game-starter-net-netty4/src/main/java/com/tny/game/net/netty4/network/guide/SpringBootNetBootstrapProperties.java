package com.tny.game.net.netty4.network.guide;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 * 不主要加 @Configuration
 * 通过 ImportNetBootstrapDefinitionRegistrar 注册
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
//@Order(HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "tny.net.bootstrap.network")
@ConditionalOnMissingBean(SpringBootNetBootstrapProperties.class)
public class SpringBootNetBootstrapProperties {

	@NestedConfigurationProperty
	private SpringNettyNetServerBootstrapSetting server;

	@NestedConfigurationProperty
	private SpringNettyNetClientBootstrapSetting client;

	//    @NestedConfigurationProperty
	private Map<String, SpringNettyNetServerBootstrapSetting> servers = ImmutableMap.of();

	//    @NestedConfigurationProperty
	private Map<String, SpringNettyNetClientBootstrapSetting> clients = ImmutableMap.of();

	public SpringNettyNetServerBootstrapSetting getServer() {
		return this.server;
	}

	public SpringBootNetBootstrapProperties setServer(SpringNettyNetServerBootstrapSetting server) {
		this.server = server;
		this.server.setName("default");
		return this;
	}

	public SpringNettyNetClientBootstrapSetting getClient() {
		return this.client;
	}

	public SpringBootNetBootstrapProperties setClient(SpringNettyNetClientBootstrapSetting client) {
		this.client = client;
		this.client.setName("default");
		return this;
	}

	public Map<String, SpringNettyNetServerBootstrapSetting> getServers() {
		return Collections.unmodifiableMap(this.servers);
	}

	public SpringBootNetBootstrapProperties setServers(Map<String, SpringNettyNetServerBootstrapSetting> servers) {
		if (servers != null) {
			servers.forEach((name, setting) -> setting.setName(name));
			this.servers = servers;
		}
		return this;
	}

	public Map<String, SpringNettyNetClientBootstrapSetting> getClients() {
		return Collections.unmodifiableMap(this.clients);
	}

	public SpringBootNetBootstrapProperties setClients(Map<String, SpringNettyNetClientBootstrapSetting> clients) {
		if (clients != null) {
			clients.forEach((name, setting) -> setting.setName(name));
			this.clients = clients;
		}
		return this;
	}

}

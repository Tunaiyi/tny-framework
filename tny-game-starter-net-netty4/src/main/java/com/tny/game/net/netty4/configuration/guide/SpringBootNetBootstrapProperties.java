package com.tny.game.net.netty4.configuration.guide;

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
@ConfigurationProperties(prefix = "tny.net")
@ConditionalOnMissingBean(SpringBootNetBootstrapProperties.class)
public class SpringBootNetBootstrapProperties {

	@NestedConfigurationProperty
	private SpringNettyServerBootstrapSetting server;

	@NestedConfigurationProperty
	private SpringNettyClientBootstrapSetting client;

	//    @NestedConfigurationProperty
	private Map<String, SpringNettyServerBootstrapSetting> servers = ImmutableMap.of();

	//    @NestedConfigurationProperty
	private Map<String, SpringNettyClientBootstrapSetting> clients = ImmutableMap.of();

	public SpringNettyServerBootstrapSetting getServer() {
		return this.server;
	}

	public SpringBootNetBootstrapProperties setServer(SpringNettyServerBootstrapSetting server) {
		this.server = server;
		return this;
	}

	public SpringNettyClientBootstrapSetting getClient() {
		return this.client;
	}

	public SpringBootNetBootstrapProperties setClient(SpringNettyClientBootstrapSetting client) {
		this.client = client;
		return this;
	}

	public Map<String, SpringNettyServerBootstrapSetting> getServers() {
		return Collections.unmodifiableMap(this.servers);
	}

	public SpringBootNetBootstrapProperties setServers(Map<String, SpringNettyServerBootstrapSetting> servers) {
		if (servers != null) {
			servers.forEach((name, setting) -> setting.setName(name));
			this.servers = servers;
		}
		return this;
	}

	public Map<String, SpringNettyClientBootstrapSetting> getClients() {
		return Collections.unmodifiableMap(this.clients);
	}

	public SpringBootNetBootstrapProperties setClients(Map<String, SpringNettyClientBootstrapSetting> clients) {
		if (clients != null) {
			clients.forEach((name, setting) -> setting.setName(name));
			this.clients = clients;
		}
		return this;
	}

}

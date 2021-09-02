package com.tny.game.net.netty4.configuration.application;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/14 6:30 下午
 */
@ConfigurationProperties("tny.app")
public class SpringNetAppProperties {

	private String name;

	private int serverId;

	private String appType = "default";

	private String scopeType = "online";

	private String locale = "zh-CN";

	private List<String> basePackages = ImmutableList.of();

	public int getServerId() {
		return this.serverId;
	}

	public SpringNetAppProperties setServerId(int serverId) {
		this.serverId = serverId;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public SpringNetAppProperties setName(String name) {
		this.name = name;
		return this;
	}

	public String getLocale() {
		return this.locale;
	}

	public SpringNetAppProperties setLocale(String locale) {
		this.locale = locale;
		return this;
	}

	public String getAppType() {
		return this.appType;
	}

	public SpringNetAppProperties setAppType(String appType) {
		this.appType = appType;
		return this;
	}

	public String getScopeType() {
		return this.scopeType;
	}

	public SpringNetAppProperties setScopeType(String scopeType) {
		this.scopeType = scopeType;
		return this;
	}

	public List<String> getBasePackages() {
		return this.basePackages;
	}

	public SpringNetAppProperties setBasePackages(List<String> basePackages) {
		this.basePackages = ImmutableList.copyOf(basePackages);
		return this;
	}

}

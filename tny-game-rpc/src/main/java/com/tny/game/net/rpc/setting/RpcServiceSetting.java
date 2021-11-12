package com.tny.game.net.rpc.setting;

import com.tny.game.common.url.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 5:02 下午
 */
public class RpcServiceSetting {

	/**
	 * rpc服务名
	 */
	private String name;

	private boolean discovery = false;

	/**
	 * 服务发现-服务名
	 */
	private String service;

	private String password = "";

	private String host;

	private int port;

	private String guide;

	private String username;

	private int connectSize = 1;

	public boolean isDiscovery() {
		return discovery;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getGuide() {
		return guide;
	}

	public boolean isHasGuide() {
		return StringUtils.isNotEmpty(guide);
	}

	public int getConnectSize() {
		return connectSize;
	}

	public String getService() {
		return service;
	}

	public String getUsername() {
		return username;
	}

	public String getServiceName() {
		if (StringUtils.isNoneBlank(service)) {
			return service;
		}
		return name;
	}

	public Optional<URL> url() {
		if (this.isDiscovery()) {
			return Optional.empty();
		}
		return Optional.of(URL.valueOf(format("rpc://{}:{}", this.getHost(), this.getPort())));
	}

	public RpcServiceSetting setName(String name) {
		this.name = name;
		return this;
	}

	public RpcServiceSetting setPassword(String password) {
		this.password = password;
		return this;
	}

	public RpcServiceSetting setHost(String host) {
		this.host = host;
		return this;
	}

	public RpcServiceSetting setPort(int port) {
		this.port = port;
		return this;
	}

	public RpcServiceSetting setConnectSize(int connectSize) {
		this.connectSize = connectSize;
		return this;
	}

	public RpcServiceSetting setService(String service) {
		this.service = service;
		return this;
	}

	public RpcServiceSetting setUsername(String username) {
		this.username = username;
		return this;
	}

	public RpcServiceSetting setGuide(String guide) {
		this.guide = guide;
		return this;
	}

	public RpcServiceSetting setDiscovery(boolean discovery) {
		this.discovery = discovery;
		return this;
	}

}

package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.cluster.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 3:42 下午
 */
public class RelayServeInstanceSetting implements ServeNode {

	private long id;

	private String url;

	private String scheme = "tcp";

	private String host;

	private int port;

	@Override
	public String getClusterId() {
		return null;
	}

	@Override
	public long getId() {
		return id;
	}

	public RelayServeInstanceSetting setId(long id) {
		this.id = id;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public RelayServeInstanceSetting setUrl(String uri) {
		this.url = uri;
		URL url = URL.valueOf(uri);
		this.scheme = url.getScheme();
		this.host = url.getHost();
		this.port = url.getPort();
		return this;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	public RelayServeInstanceSetting setScheme(String scheme) {
		this.scheme = scheme;
		return this;
	}

	@Override
	public String getHost() {
		return host;
	}

	public RelayServeInstanceSetting setHost(String host) {
		this.host = host;
		return this;
	}

	@Override
	public int getPort() {
		return port;
	}

	public RelayServeInstanceSetting setPort(int port) {
		this.port = port;
		return this;
	}

}

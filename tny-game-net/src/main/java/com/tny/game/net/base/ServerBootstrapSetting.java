package com.tny.game.net.base;

import java.net.InetSocketAddress;

public interface ServerBootstrapSetting extends NetBootstrapSetting {

	String serviceName();

	InetSocketAddress bindAddress();

	InetSocketAddress serveAddress();

}

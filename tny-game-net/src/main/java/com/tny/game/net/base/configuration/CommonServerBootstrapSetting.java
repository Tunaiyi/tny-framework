package com.tny.game.net.base.configuration;

import com.tny.game.net.base.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.InetSocketAddress;

public class CommonServerBootstrapSetting extends CommonNetBootstrapSetting implements ServerBootstrapSetting {

	private InetSocketAddress bindAddress;

	private InetSocketAddress serveAddress;

	private String bindAddressValue;

	private String serveAddressValue;

	public CommonServerBootstrapSetting() {
	}

	@Override
	public InetSocketAddress bindAddress() {
		return this.bindAddress;
	}

	@Override
	public InetSocketAddress serveAddress() {
		return serveAddress;
	}

	public String getBindAddress() {
		return this.bindAddressValue;
	}

	public void setBindAddress(String address) {
		this.bindAddressValue = address;
		String[] hostPort = StringUtils.split(address, ":");
		this.bindAddress = new InetSocketAddress(hostPort[0], NumberUtils.toInt(hostPort[1]));
	}

	public String getServeAddress() {
		return this.serveAddressValue;
	}

	public void setServeAddress(String address) {
		this.serveAddressValue = address;
		String[] hostPort = StringUtils.split(address, ":");
		this.serveAddress = new InetSocketAddress(hostPort[0], NumberUtils.toInt(hostPort[1]));
	}

}

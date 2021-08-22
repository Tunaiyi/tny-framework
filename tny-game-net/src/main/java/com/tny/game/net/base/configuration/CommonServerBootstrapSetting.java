package com.tny.game.net.base.configuration;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.InetSocketAddress;
import java.util.*;

public class CommonServerBootstrapSetting extends CommonNetBootstrapSetting implements ServerBootstrapSetting {

	private Collection<InetSocketAddress> bindAddressList;

	private List<String> bind;

	public CommonServerBootstrapSetting() {
	}

	@Override
	public Collection<InetSocketAddress> getBindAddressList() {
		return this.bindAddressList;
	}

	public List<String> getBind() {
		return this.bind;
	}

	public void setBind(List<String> bindAddresses) {
		this.bind = bindAddresses;
		Collection<InetSocketAddress> addressList = new ArrayList<>();
		for (String address : bindAddresses) {
			String[] hostPort = StringUtils.split(address, ":");
			addressList.add(new InetSocketAddress(hostPort[0], NumberUtils.toInt(hostPort[1])));
		}
		this.bindAddressList = ImmutableList.copyOf(addressList);
	}

}

package com.tny.game.net.base.configuration;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.InetSocketAddress;
import java.util.*;

public class CommonServerBootstrapSetting extends CommonNetBootstrapSetting implements ServerBootstrapSetting {

    private Collection<InetSocketAddress> bindAddresses;

    public CommonServerBootstrapSetting() {
    }

    public CommonServerBootstrapSetting(NetAppContext appContext) {
        super(appContext);
    }

    @Override
    public Collection<InetSocketAddress> getBindAddresses() {
        return this.bindAddresses;
    }

    public CommonServerBootstrapSetting setBindAddresses(Collection<InetSocketAddress> bindAddresses) {
        this.bindAddresses = bindAddresses;
        return this;
    }

    public void setBind(String[] bindAddresses) {
        Collection<InetSocketAddress> addressList = new ArrayList<>();
        for (String address : bindAddresses) {
            String[] hostPort = StringUtils.split(address, ":");
            addressList.add(new InetSocketAddress(hostPort[0], NumberUtils.toInt(hostPort[1])));
        }
        this.bindAddresses = ImmutableList.copyOf(addressList);
    }

}

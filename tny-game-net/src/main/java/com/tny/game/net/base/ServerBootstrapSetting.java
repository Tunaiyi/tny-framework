package com.tny.game.net.base;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface ServerBootstrapSetting extends NetBootstrapSetting {

    Collection<InetSocketAddress> getBindAddressList();

}

package com.tny.game.net.base;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface ServerUnitSetting extends NetUnitSetting {

    Collection<InetSocketAddress> getBindAddresses();
}

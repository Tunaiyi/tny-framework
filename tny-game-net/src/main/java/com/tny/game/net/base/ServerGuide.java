package com.tny.game.net.base;

import com.tny.game.net.base.listener.*;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface ServerGuide {

	String getName();

	void open();

	boolean isBound();

	boolean close();

	String getScheme();

	InetSocketAddress getBindAddress();

	InetSocketAddress getServeAddress();

	void addClosedListener(ServerClosedListener listener);

	void addClosedListeners(Collection<ServerClosedListener> listenerCollection);

	void clearClosedListener();

}

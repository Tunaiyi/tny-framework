package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

import java.util.List;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public interface NetTerminal<UID> extends NetEndpoint<UID>, Terminal<UID> {

	long getConnectTimeout();

	int getConnectRetryTimes();

	List<Long> getConnectRetryIntervals();

	boolean isAsyncConnect();

	MessageTransporter connect();

	void reconnect();

	void onConnected(NetTunnel<UID> tunnel);

}

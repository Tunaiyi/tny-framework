package com.tny.game.net.netty4.network.configuration.event;

import com.tny.game.net.netty4.configuration.application.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/8 2:58 下午
 */
public class NetApplicationStopEvent extends NetApplicationEvent {

	public NetApplicationStopEvent(NetApplication application) {
		super(application);
	}

}

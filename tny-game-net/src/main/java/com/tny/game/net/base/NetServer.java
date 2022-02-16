package com.tny.game.net.base;

import com.tny.game.net.serve.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 8:36 下午
 */
public interface NetServer extends Serve {

	/**
	 * @return 服务名
	 */
	String getName();

	@Override
	default String getService() {
		return getName();
	}

	String getScheme();

	InetSocketAddress getBindAddress();

	InetSocketAddress getServeAddress();

}

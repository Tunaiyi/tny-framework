package com.tny.game.net.base;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 8:36 下午
 */
public interface NetServer {

	String getName();

	String getScheme();

	InetSocketAddress getBindAddress();

	InetSocketAddress getServeAddress();

}

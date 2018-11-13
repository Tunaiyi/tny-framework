package com.tny.game.net.base;

import com.tny.game.net.transport.NetTunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-01 10:04
 */
public interface PostConnect<UID> {

    boolean onConnected(NetTunnel<UID> tunnel) throws Exception;

}

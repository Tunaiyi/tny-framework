package com.tny.game.net.transport;


/**
 * 会话管理者
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 15:37
 */
public interface SessionKeeperFactory {

    <UID> NetSessionKeeper<UID> getKeeper(String userType);

}

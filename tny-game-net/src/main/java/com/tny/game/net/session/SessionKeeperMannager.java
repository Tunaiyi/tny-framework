package com.tny.game.net.session;


/**
 * 会话管理者
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 15:37
 */
public interface SessionKeeperMannager {

    <UID> SessionKeeper<UID> getKeeper(String userType);

}

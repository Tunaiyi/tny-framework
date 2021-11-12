package com.tny.game.net.relay.link;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/3 8:47 下午
 */
public interface LocalServeClusterSetting {

	String getServeName();

	String getUsername();

	long getLinkMaxIdleTime();

	long getLinkHeartbeatInterval();

	int getLinkConnectionSize();

	boolean isDiscoveryEnable();

}

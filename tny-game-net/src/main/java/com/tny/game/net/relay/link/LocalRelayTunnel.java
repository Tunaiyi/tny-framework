package com.tny.game.net.relay.link;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:57 下午
 */
public interface LocalRelayTunnel<UID> extends NetRelayTunnel<UID> {

	/**
	 * 切换 link
	 *
	 * @param link 切换的 link
	 */
	boolean switchLink(LocalRelayLink link);

}

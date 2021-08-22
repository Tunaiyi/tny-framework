package com.tny.game.relay.transport;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 3:42 上午
 */
public interface RelayListRouter {

	NetRelayLink rout(List<NetRelayLink> relayLinks);

}

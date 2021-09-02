package com.tny.game.net.relay.link.listener;

import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 2:08 上午
 */
public interface RelayLinkListener {

	void onOpen(NetRelayLink link);

	void onClosing(NetRelayLink link);

	void onClosed(NetRelayLink link);

}

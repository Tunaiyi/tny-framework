package com.tny.game.net.relay.link;

import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:45
 */
public interface RemoteRelayMessageTransporter<UID> extends MessageTransporter<UID> {

	/**
	 * 切换 link
	 *
	 * @param link 新 link
	 * @return 返回是否切换成功
	 */
	boolean switchLink(RemoteRelayLink link);

}

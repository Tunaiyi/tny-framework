package com.tny.game.net.relay.link.allot;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 8:11 下午
 */
public interface LocalRelayLinkAllotStrategy {

	LocalRelayLink allot(Tunnel<?> tunnel, LocalServeInstance instance);

}
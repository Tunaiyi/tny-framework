package com.tny.game.net.relay.link.exception;

import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:00 下午
 */
public class RelayLinkNoExistException extends RelayLinkException {

    public RelayLinkNoExistException(String message, Object... messageParams) {
        super(NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR, message, messageParams);
    }

}

package com.tny.game.net.relay.link.route;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;

/**
 * 转发消息路由器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:42 下午
 */
public class FixedRelayMessageRouter implements RelayMessageRouter {

    private final String service;

    public FixedRelayMessageRouter(String service) {
        this.service = service;
    }

    @Override
    public String route(RemoteRelayTunnel<?> tunnel, MessageSchema schema) {
        return service;
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.relay.packet.arguments;

import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:05 下午
 */
public class TunnelRelayArguments extends BaseTunnelPacketArguments {

    private final Message message;

    public TunnelRelayArguments(long instanceId, long tunnelId, Message message) {
        super(instanceId, tunnelId);
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public void release() {
        Object body = message.getBody();
        if (body instanceof OctetMessageBody) {
            ((OctetMessageBody) body).release();
        }
    }

}

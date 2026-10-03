/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

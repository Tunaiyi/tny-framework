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
package com.tny.game.net.relay;

import com.tny.game.net.application.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/11/7 04:53
 **/
public class RelayMonitor {

    public RelayMonitor() {
    }

    public void onReadPacket(NetRelayLink link, RelayPacket<?> packet) {
        NetLogger.logReceive(link, packet);
    }

    public void onWritePacket(NetRelayLink link, RelayPacket<?> packet) {
        NetLogger.logSend(link, packet);
    }

    public void onLinkOpen(RelayTransport transport, LinkOpenPacket packet) {
        NetLogger.logReceive(transport, packet);
    }

}

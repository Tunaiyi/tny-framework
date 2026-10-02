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

import com.tny.game.net.relay.link.*;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class TunnelConnectArguments extends BaseTunnelPacketArguments {

    private final int[] ipValue;

    private final String ip;

    private final int port;

    public TunnelConnectArguments(RelayTunnel tunnel, byte[] ip, int port) {
        this(tunnel.getInstanceId(), tunnel.getId(), ip, port);
    }

    public TunnelConnectArguments(long instanceId, long tunnelId, byte[] ip, int port) {
        super(instanceId, tunnelId);
        this.ipValue = new int[4];
        for (int i = 0; i < ipValue.length; i++) {
            ipValue[i] = ip[i] & 0xff;
        }
        this.ip = StringUtils.join(ipValue, '.');
        this.port = port;
    }

    public String getIp() {
        return this.ip;
    }

    public int[] getIpValue() {
        return ipValue;
    }

    public int getPort() {
        return this.port;
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public class ConnectIdFactory {

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong(0);

    private static final AtomicLong TRANSPORT_ID_CREATOR = new AtomicLong(0);

    private static final AtomicLong SESSION_ID_CREATOR = new AtomicLong(0);

    public static long newEndpointId() {
        return SESSION_ID_CREATOR.incrementAndGet();
    }

    public static long newTunnelId() {
        return TUNNEL_ID_CREATOR.incrementAndGet();
    }

    public static long newTransportId() {
        return TRANSPORT_ID_CREATOR.incrementAndGet();
    }

}

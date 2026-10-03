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

package com.tny.game.net.transport;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public class ConnectIdFactory {

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong(0);

    private static final AtomicLong TRANSPORT_ID_CREATOR = new AtomicLong(0);

    private static final AtomicLong SESSION_ID_CREATOR = new AtomicLong(0);

    public static long newSessionId() {
        return SESSION_ID_CREATOR.incrementAndGet();
    }

    public static long newTunnelId() {
        return TUNNEL_ID_CREATOR.incrementAndGet();
    }

    public static long newTransportId() {
        return TRANSPORT_ID_CREATOR.incrementAndGet();
    }

}

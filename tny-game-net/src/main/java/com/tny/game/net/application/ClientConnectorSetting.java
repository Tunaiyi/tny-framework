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

package com.tny.game.net.application;

import com.tny.game.net.utils.*;

import java.util.*;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 4:52 下午
 */
public class ClientConnectorSetting {

    private int retryTimes = NetConfigs.RETRY_TIMES_DEFAULT_VALUE;

    private boolean autoReconnect = AUTO_RECONNECT_DEFAULT_VALUE;

    private List<Long> retryIntervals = new ArrayList<>(List.of(RETRY_INTERVAL_DEFAULT_VALUE));

    private long connectTimeout = NetConfigs.CONNECT_TIMEOUT_DEFAULT_VALUE;

    public int getRetryTimes() {
        return retryTimes;
    }

    public List<Long> getRetryIntervals() {
        return retryIntervals;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public ClientConnectorSetting setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public ClientConnectorSetting setRetryIntervals(List<Long> retryIntervals) {
        this.retryIntervals = new ArrayList<>(retryIntervals);
        return this;
    }

    public ClientConnectorSetting setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public ClientConnectorSetting setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

}

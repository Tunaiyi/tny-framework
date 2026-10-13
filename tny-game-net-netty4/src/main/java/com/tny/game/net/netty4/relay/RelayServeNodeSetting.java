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

package com.tny.game.net.netty4.relay;

import com.tny.game.net.application.*;
import com.tny.game.net.relay.cluster.*;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 3:42 下午
 */
public class RelayServeNodeSetting extends BaseServeNode implements ServedService {

    public RelayServeNodeSetting() {
    }

    public RelayServeNodeSetting(String appType, String scopeType, String serveName, String service,
            NetAccessNode point) {
        super(appType, scopeType, serveName, service, point);
    }

    public RelayServeNodeSetting(String serveName, String service, String appType, String scopeType, long id, String scheme, String host,
            int port) {
        super(serveName, service, appType, scopeType, id, scheme, host, port);
    }

    @Override
    public RelayServeNodeSetting setId(long id) {
        super.setId(id);
        return this;
    }

    @Override
    public RelayServeNodeSetting setHealthy(boolean healthy) {
        super.setHealthy(healthy);
        return this;
    }

    @Override
    public RelayServeNodeSetting setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    @Override
    public RelayServeNodeSetting setHost(String host) {
        super.setHost(host);
        return this;
    }

    @Override
    public RelayServeNodeSetting setPort(int port) {
        super.setPort(port);
        return this;
    }

    @Override
    public RelayServeNodeSetting setMetadata(Map<String, Object> metadata) {
        super.setMetadata(metadata);
        return this;
    }

    @Override
    public RelayServeNodeSetting setUrl(String value) {
        super.setUrl(value);
        return this;
    }

}

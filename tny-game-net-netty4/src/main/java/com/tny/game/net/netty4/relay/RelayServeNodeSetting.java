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

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

package com.tny.game.net.relay.cluster;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class RemoteServeNode extends BaseServeNode {

    private long launchTime;

    public RemoteServeNode() {
    }

    public RemoteServeNode(String appType, String scopeType, String serveName, String service, NetAccessNode point) {
        super(appType, scopeType, serveName, service, point);
    }

    public RemoteServeNode(String serveName, String service, String appType, String scopeType, long id, String scheme, String host, int port) {
        super(serveName, service, appType, scopeType, id, scheme, host, port);
    }

    public long getLaunchTime() {
        return launchTime;
    }

    protected RemoteServeNode setLaunchTime(long launchTime) {
        this.launchTime = launchTime;
        return this;
    }

}

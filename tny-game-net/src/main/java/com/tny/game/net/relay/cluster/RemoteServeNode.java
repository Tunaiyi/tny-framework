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

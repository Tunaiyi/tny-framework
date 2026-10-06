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

package com.tny.game.net.rpc.auth;

import com.tny.game.net.application.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:42 上午
 */
public class RpcAccessToken extends RpcAccessIdentify {

    private RpcAccessIdentify user;

    private long issueAt;

    public RpcAccessToken() {
    }

    public RpcAccessToken(RpcServiceType serviceType, int serverId, RpcAccessIdentify user) {
        super(serviceType, serverId, user.getIndex());
        this.user = user;
        this.issueAt = System.currentTimeMillis();
    }

    public String getService() {
        return this.getServiceType().getService();
    }

    public RpcAccessIdentify getUser() {
        return user;
    }

    public long getIssueAt() {
        return issueAt;
    }

    private RpcAccessToken setIssueAt(long issueAt) {
        this.issueAt = issueAt;
        return this;
    }

    public RpcAccessToken setUser(RpcAccessIdentify user) {
        this.user = user;
        return this;
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.rpc.auth;

import com.tny.game.net.base.*;

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

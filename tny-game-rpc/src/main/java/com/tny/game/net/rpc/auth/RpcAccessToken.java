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

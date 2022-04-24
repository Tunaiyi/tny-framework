package com.tny.game.net.rpc.auth;

import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:42 上午
 */
public class RpcToken extends RpcLinkerId {

    private RpcLinkerId user;

    private long issueAt;

    public RpcToken() {
    }

    public RpcToken(String service, long serverId, long id, RpcLinkerId user) {
        super(service, serverId, id);
        this.user = user;
        this.issueAt = System.currentTimeMillis();
    }

    public RpcLinkerId getUser() {
        return user;
    }

    public long getIssueAt() {
        return issueAt;
    }

    private RpcToken setIssueAt(long issueAt) {
        this.issueAt = issueAt;
        return this;
    }

    public RpcToken setUser(RpcLinkerId user) {
        this.user = user;
        return this;
    }

}

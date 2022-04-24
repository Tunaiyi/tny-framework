package com.tny.game.net.rpc.auth;

import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:42 上午
 */
public class RpcToken extends RpcAccessId {

    private RpcAccessId user;

    private long issueAt;

    public RpcToken() {
    }

    public RpcToken(String service, long serverId, long id, RpcAccessId user) {
        super(service, serverId, id);
        this.user = user;
        this.issueAt = System.currentTimeMillis();
    }

    public RpcAccessId getUser() {
        return user;
    }

    public long getIssueAt() {
        return issueAt;
    }

    private RpcToken setIssueAt(long issueAt) {
        this.issueAt = issueAt;
        return this;
    }

    public RpcToken setUser(RpcAccessId user) {
        this.user = user;
        return this;
    }

}

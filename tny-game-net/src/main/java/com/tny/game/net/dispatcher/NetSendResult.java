package com.tny.game.net.dispatcher;

import com.tny.game.net.base.SendResult;
import com.tny.game.net.session.Session;
import io.netty.channel.ChannelFuture;

public class NetSendResult implements SendResult {

    private long userID;
    private String userGroup;
    private Throwable cause;
    private boolean done;
    private boolean success;

    public NetSendResult(Session session, ChannelFuture future) {
        super();
        this.userGroup = session.getGroup();
        this.userID = session.getUID();
        this.cause = future.cause();
        this.done = future.isDone();
        this.success = future.isSuccess();
    }

    public NetSendResult(long userID, ChannelFuture future) {
        super();
        this.userGroup = Session.DEFAULT_USER_GROUP;
        this.userID = userID;
        this.cause = future.cause();
        this.done = future.isDone();
        this.success = future.isSuccess();
    }

    @Override
    public long getUserID() {
        return userID;
    }

    @Override
    public String getUserGroup() {
        return userGroup;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

}

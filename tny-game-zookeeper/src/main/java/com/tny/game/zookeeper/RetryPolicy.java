package com.tny.game.zookeeper;

import org.apache.zookeeper.KeeperException.Code;

public abstract class RetryPolicy {

    protected abstract void fail(Code code);

    protected abstract void success();

    protected abstract void resset();

    public abstract long getDelayTime();

    public abstract boolean hasNext();

}

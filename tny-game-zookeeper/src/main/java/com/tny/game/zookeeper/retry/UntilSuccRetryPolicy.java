package com.tny.game.zookeeper.retry;

import com.tny.game.zookeeper.RetryPolicy;
import org.apache.zookeeper.KeeperException.Code;

public class UntilSuccRetryPolicy extends RetryPolicy {

    private int interval;

    private boolean fail = false;

    public UntilSuccRetryPolicy(int interval) {
        super();
        this.interval = interval;
    }

    @Override
    protected void fail(Code code) {
        fail = true;
    }

    @Override
    protected void success() {
        fail = false;
    }

    @Override
    protected void reset() {
        fail = false;
    }

    @Override
    public long getDelayTime() {
        return !fail ? 0 : interval;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

}

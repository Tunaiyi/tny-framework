package com.tny.game.oplog;

public interface OpLogExecutor {

    void submit(OpLog opLog);

    void shutdown();
}

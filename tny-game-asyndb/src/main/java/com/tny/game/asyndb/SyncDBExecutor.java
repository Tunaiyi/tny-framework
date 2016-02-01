package com.tny.game.asyndb;

/**
 * 同步线程执行器
 *
 * @author KGTny
 */
public interface SyncDBExecutor {

    public boolean sumit(Synchronizable synchronizable);

    public boolean shutdown() throws InterruptedException;

}

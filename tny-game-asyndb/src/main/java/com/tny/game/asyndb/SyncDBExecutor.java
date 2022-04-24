package com.tny.game.asyndb;

/**
 * 同步线程执行器
 *
 * @author KGTny
 */
public interface SyncDBExecutor {

    boolean summit(PersistentObject object);

    boolean shutdown() throws InterruptedException;

}

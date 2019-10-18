package com.tny.game.data.storage;

/**
 * 同步线程执行器
 *
 * @author KGTny
 */
public interface StorageExecutor {

    // public boolean sumit(PersistentObject synchronizable);

    boolean shutdown() throws InterruptedException;

}

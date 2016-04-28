package com.tny.game.worker;

/**
 * 世界工作器
 *
 * @author KGTny
 */
public interface CommandExecutor extends CommandWorker {

    String getName();

    boolean isRunning();

    void stop();

    int size();

    void shutdown();

    void start();

}

package com.tny.game.common.worker;

/**
 * 世界工作器
 *
 * @author KGTny
 */
public interface CommandExecutor extends CommandBoxProcessor {

    String getName();

    void stop();

    int size();

    void shutdown();

    void start();

}

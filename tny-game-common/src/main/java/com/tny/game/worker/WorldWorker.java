package com.tny.game.worker;

/**
 * 世界工作器
 *
 * @author KGTny
 */
public interface WorldWorker {

    public abstract String getName();

    public abstract void stop();

    public abstract Thread getWorkerThread();

    public abstract boolean register(CommandBox commandBox);

    public abstract boolean unregister(CommandBox commandBox);

    public abstract boolean isRunning();

    public abstract int size();

    public abstract void release();

    public abstract void start();

    public abstract CommandBox getCommandBox();
}

package com.tny.game.worker;

/**
 * 世界工作器
 *
 * @author KGTny
 */
public interface CommandWorker extends CommandBoxProcessor {

    boolean isOnCurrentThread();

    @Override
    default boolean register(CommandBox commandBox) {
        return commandBox.bindWorker(this);
    }

    @Override
    default boolean unregister(CommandBox commandBox) {
        return commandBox.unbindWorker();
    }

    default void submit(CommandBox commandBox) {
        commandBox.process();
    }

}

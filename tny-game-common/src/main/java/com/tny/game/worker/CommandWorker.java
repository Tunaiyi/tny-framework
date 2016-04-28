package com.tny.game.worker;

/**
 * 世界工作器
 *
 * @author KGTny
 */
public interface CommandWorker {

    boolean isOnCurrentThread();

    default boolean register(CommandBox commandBox) {
        return commandBox.bindWorker(this);
    }

    default boolean unregister(CommandBox commandBox) {
        return commandBox.unbindWorker();
    }

    default void run(CommandBox box) {
        box.run();
    }

}

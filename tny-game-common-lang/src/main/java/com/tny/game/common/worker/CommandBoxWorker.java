package com.tny.game.common.worker;

/**
 * 世界工作器
 *
 * @author KGTny
 */
public interface CommandBoxWorker extends CommandBoxProcessor {

    boolean isOnCurrentThread();

    @Override
    default boolean register(CommandBox<?> commandBox) {
        return commandBox.bindWorker(this);
    }

    @Override
    default boolean unregister(CommandBox<?> commandBox) {
        return commandBox.unbindWorker();
    }

    /**
     * 通知执行器
     *
     * @param commandBox 执行
     */
    void wakeUp(CommandBox<?> commandBox);

}
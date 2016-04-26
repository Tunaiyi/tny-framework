package com.tny.game.worker.command;

/**
 * 命令借口
 *
 * @author KGTny
 */
public interface Command {

    /**
     * @return 执行返回
     */
    void execute();

    /**
     * @return 是否有效
     */
    default boolean isWork() {
        return true;
    }

    /**
     * @return 是否成功完成
     */
    boolean isDone();

    /**
     * @return 获取命令名字
     */
    default String getName() {
        return this.getClass().getName();
    }

}

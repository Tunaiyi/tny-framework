package com.tny.game.common.worker.command;

/**
 * 命令借口
 *
 * @author KGTny
 */
public interface Command {

    /**
     * 执行
     */
    void execute();

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

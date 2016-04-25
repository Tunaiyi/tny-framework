package com.tny.game.worker.command;

/**
 * 命令借口
 *
 * @param <O>
 * @author KGTny
 */
public interface Command<O> {

    /**
     * @return 执行返回
     */
    O execute();

    /**
     * @return 是否处理完成(包括成功, 失效)
     */
    boolean isDone();

    /**
     * @return 是否成功完成
     */
    boolean isCompleted();

    /**
     * @return 获取命令名字
     */
    default String getName() {
        return this.getClass().getName();
    }

}

package com.tny.game.worker.command;

/**
 * 命令借口
 *
 * @param <O>
 * @author KGTny
 */
public interface Command<O> {

    /**
     * 执行返回
     *
     * @return
     */
    public abstract O execute();

    /**
     * 获取执行延迟时间
     *
     * @return
     */
    public boolean isCanExecute();

    /**
     * 获取命令名字
     *
     * @return
     */
    public String getName();

    /**
     * 是否有效
     *
     * @return
     */
    public boolean isWorking();

    /**
     * 是否完成
     *
     * @return
     */
    public boolean isCompleted();

}

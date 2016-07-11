package com.tny.game.actor;


/**
 * 响应的未来对象
 *
 * @author KGTny
 */
public interface VoidAnswer extends Answer<Void>, BeFinished {

    /**
     * @param listener 添加未来响应监听器
     */
    void addListener(VoidAnswerListener listener);

    @Override
    default boolean isFinished() {
        return this.isDone();
    }

}

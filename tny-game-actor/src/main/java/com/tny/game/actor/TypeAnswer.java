package com.tny.game.actor;


/**
 * 响应的未来对象
 *
 * @param <V>
 * @author KGTny
 */
public interface TypeAnswer<V> extends Answer<V> {

    /**
     * @param listener 添加未来响应监听器
     */
    void addListener(AnswerListener<V> listener);

}

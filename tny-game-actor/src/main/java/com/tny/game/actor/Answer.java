package com.tny.game.actor;

import java.util.concurrent.Future;

/**
 * 响应的未来对象
 *
 * @author KGTny
 */
public interface Answer<V> extends Future<V>, DoneSupplier<V> {

    boolean isFail();

    void addListener(AnswerListener<V> listener);
    Throwable getCause();

    @Override
    default boolean isCompleted() {
        return this.isDone();
    }

}

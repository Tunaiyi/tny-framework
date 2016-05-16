package com.tny.game.actor;

import java.util.concurrent.Future;

/**
 * 响应的未来对象
 *
 * @author KGTny
 */
public interface Answer<V> extends Future<V> {

    boolean isFail();

    Throwable getCause();

}

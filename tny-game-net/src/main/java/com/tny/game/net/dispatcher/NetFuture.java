package com.tny.game.net.dispatcher;

import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/8/9.
 */
public interface NetFuture extends Future<Void> {

    /**
     * @return 获取对应的Session
     */
    Session getSession();


    /**
     * Returns {@code true} if and only if the I/O operation was completed
     * successfully.
     */
    boolean isSuccess();

    /**
     * Returns the cause of the failed I/O operation if the I/O operation has
     * failed.
     *
     * @return the cause of the failure.
     * {@code null} if succeeded or this future is not
     * completed yet.
     */
    Throwable cause();


    /**
     * Adds the specified listener to this future.  The
     * specified listener is notified when this future is
     * {@linkplain #isDone() done}.  If this future is already
     * completed, the specified listener is notified immediately.
     */
    NetFuture addListener(Consumer<NetFuture> listener);

    /**
     * Removes the specified listener from this future.
     * The specified listener is no longer notified when this
     * future is {@linkplain #isDone() done}.  If the specified
     * listener is not associated with this future, this method
     * does nothing and returns silently.
     */
    NetFuture removeListener(Consumer<NetFuture> listener);

}

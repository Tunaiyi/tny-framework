package com.tny.game.net.message;

/**
 * Created by Kun Yang on 2017/9/12.
 */
public interface MessageWriteFuture<UID> {

    MessageContent<UID> getContent();

    /**
     * @return 发送Future
     */
    default boolean isHasFuture() {
        return this.getContent().isHasFuture();
    }

    default void cancel(boolean mayInterruptIfRunning) {
        this.getContent().cancelSendWait(mayInterruptIfRunning);
    }

    default void success(Message<UID> message) {
        this.getContent().sendSuccess(message);
    }

    default void fail(Throwable cause) {
        this.getContent().sendFailed(cause);
    }

}

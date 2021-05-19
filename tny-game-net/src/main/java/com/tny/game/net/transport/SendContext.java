package com.tny.game.net.transport;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface SendContext<UID> {

    /**
     * @return 获取发送超时
     */
    long getWriteTimeout();

    /**
     * @return 获取响应 Future, 如果没有返回 null
     */
    RespondFuture getRespondFuture();

    /**
     * @return 是否有响应 Future
     */
    boolean isHasRespondFuture();

    /**
     * @return 获取发送 Future, 如果没有返回 null
     */
    WriteMessageFuture getWriteFuture();

    /**
     * @return 是否有发送 Future
     */
    boolean isHasWriteFuture();

}

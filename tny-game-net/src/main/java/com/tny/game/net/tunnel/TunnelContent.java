package com.tny.game.net.tunnel;

import com.tny.game.net.message.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface TunnelContent<UID> {

    /**
     * @return 发送消息
     */
    Message<UID> getMessage();

    /**
     * @return 发送Future
     */
    CompletableFuture<Tunnel<UID>> getSendFuture();

    /**
     * 取消发送等待
     *
     * @param mayInterruptIfRunning 是否打断等待线程
     */
    void cancelSendWait(boolean mayInterruptIfRunning);

}

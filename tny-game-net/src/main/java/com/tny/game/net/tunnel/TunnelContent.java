package com.tny.game.net.tunnel;

import com.tny.game.net.message.Message;

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
    boolean hasSendFuture();

    /**
     * 取消发送等待
     *
     * @param mayInterruptIfRunning 是否打断等待线程
     */
    void cancelSendWait(boolean mayInterruptIfRunning);

    /**
     * 发送成功
     */
    void sendSuccess(Tunnel<UID> tunnel);

    /**
     * 发送失败
     */
    void sendFailed(Throwable cause);

}

package com.tny.game.net.transport;

import com.tny.game.net.message.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class MessageContext implements SendReceipt, MessageContent {

    //    /**
    //     * @return 获取结果码
    //     */
    //    public abstract ResultCode getResultCode();

    /**
     * @param header 头部信息
     * @return 返回 context 自身
     */
    public abstract MessageContext withHeader(MessageHeader<?> header);

    /**
     * @param headers 头部信息列表
     * @return 返回 context 自身
     */
    public abstract MessageContext withHeaders(Collection<MessageHeader<?>> headers);

    /**
     * @param body 设置 Message Body
     * @return 返回 context 自身
     */
    public abstract MessageContext withBody(Object body);

    /**
     * 设置写出等待对象
     *
     * @return 返回 context 自身
     */
    public abstract MessageContext willWriteAwaiter();

    /**
     * 获取写出等待对象
     *
     * @return 返回 context 自身
     */
    public abstract MessageWriteAwaiter getWriteAwaiter();

    /**
     * 取消
     *
     * @param mayInterruptIfRunning 打断所欲运行
     */
    public abstract void cancel(boolean mayInterruptIfRunning);

    /**
     * 取消
     */
    public abstract void cancel(Throwable throwable);

}
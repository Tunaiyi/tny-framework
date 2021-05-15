package com.tny.game.net.passthrough;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 11:45 上午
 */
public interface NetPipe<UID> extends Pipe<UID> {

    /**
     * 移出指定 tubule
     *
     * @param tubule 移出的tubule
     */
    void destroyTubule(NetTubule<UID> tubule);

    /**
     * 写出消息
     *
     * @param tubule  发送tubule
     * @param message 发送消息
     * @param promise 发送等待对象
     */
    void write(NetTubule<UID> tubule, Message message, WriteMessagePromise promise);

    /**
     * 创建写出消息等待对象
     *
     * @param timeout 等待时间
     * @return 返回
     */
    WriteMessagePromise createWritePromise(long timeout);

}

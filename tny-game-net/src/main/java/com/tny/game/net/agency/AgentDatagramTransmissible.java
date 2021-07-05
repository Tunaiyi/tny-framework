package com.tny.game.net.agency;

import com.tny.game.net.agency.datagram.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 4:16 下午
 */
public interface AgentDatagramTransmissible {

    /**
     * 写出数据
     *
     * @param datagram 数据包
     * @param promise  写出Promise
     * @return 返回 WriteMessageFuture
     */
    WriteMessageFuture write(TubuleDatagram datagram, WriteMessagePromise promise);

    /**
     * 写出数据
     *
     * @param datagram 数据包
     * @param promise  写出Promise
     * @return 返回 WriteMessageFuture
     */
    WriteMessageFuture write(AgentDatagramMaker datagram, WriteMessagePromise promise);

    /**
     * 创建写出Promise
     *
     * @param timeout 超时时间
     * @return 返回写出Promise
     */
    WriteMessagePromise createWritePromise(long timeout);

}

package com.tny.game.net.relay;

import com.tny.game.net.relay.packet.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 4:16 下午
 */
public interface RelayPacketTransmissible {

    /**
     * 写出数据
     *
     * @param packet  数据包
     * @param promise 写出Promise
     * @return 返回 WriteMessageFuture
     */
    WriteMessageFuture write(RelayPacket packet, WriteMessagePromise promise);

    /**
     * 写出数据
     *
     * @param maker   数据包构建器
     * @param promise 写出Promise
     * @return 返回 WriteMessageFuture
     */
    WriteMessageFuture write(RelayPacketMaker maker, WriteMessagePromise promise);

    /**
     * 创建写出Promise
     *
     * @param timeout 超时时间
     * @return 返回写出Promise
     */
    WriteMessagePromise createWritePromise(long timeout);

}

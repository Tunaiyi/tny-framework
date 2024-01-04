/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.relay.link;

import com.tny.game.net.application.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.transport.*;

import java.util.function.Consumer;

/**
 * 转发数据发送器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:13 下午
 */
public interface RelayTransporter extends Connection {

    // /**
    //  * @return 接入模式
    //  */
    // NetAccessMode getAccessMode();
    //

    /**
     * @param onClose 注册关闭监听器
     */
    void addCloseListener(Consumer<RelayTransporter> onClose);

    /**
     * 绑定转发线路
     *
     * @param link 转发线路
     */
    void bind(NetRelayLink link);

    /**
     * @return 获取启动器上下文
     */
    NetworkContext getContext();

    /**
     * 写出数据
     *
     * @param packet  数据包
     * @param awaiter 写出Promise
     * @return 返回 MessageWriteAwaiter
     */
    MessageWriteFuture write(RelayPacket<?> packet, MessageWriteFuture awaiter);

    /**
     * 写出数据
     *
     * @param maker 数据包构建器
     * @return 返回 MessageWriteAwaiter
     */
    MessageWriteFuture write(RelayPacketMaker maker, MessageWriteFuture awaiter);

}

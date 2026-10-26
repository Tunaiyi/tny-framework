/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public interface RelayTransport extends Connection {

    // /**
    //  * @return 接入模式
    //  */
    // NetAccessMode getAccessMode();
    //

    /**
     * @param onClose 注册关闭监听器
     */
    void addCloseListener(Consumer<RelayTransport> onClose);

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

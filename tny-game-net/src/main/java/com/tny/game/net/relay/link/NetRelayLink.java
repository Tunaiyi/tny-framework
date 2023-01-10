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

import com.tny.game.common.event.firer.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.listener.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.transport.*;

/**
 * 转发链接
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 11:45 上午
 */
public interface NetRelayLink extends RelayLink, EventSourceObject<RelayLinkListener> {

    static String idOf(NetRelayLink relayLink) {
        return idOf(relayLink.getService(), relayLink.getInstanceId(), relayLink.getKey());
    }

    static String idOf(String service, long id, String key) {
        return service + "-" + id + "-" + key;
    }

    /**
     * @param tunnel 关闭管道
     */
    void closeTunnel(RelayTunnel<?> tunnel);

    /**
     * @param tunnel 打开管道
     */
    void openTunnel(RelayTunnel<?> tunnel);

    /**
     * 判断指定的 transporter 是否是当前通道的 transporter
     *
     * @param transporter 判断的transporter
     * @return 如果是返回 true, 否则返回 false
     */
    boolean isCurrentTransporter(RelayTransporter transporter);

    /**
     * 发送转发数据包
     *
     * @param factory   数据包工厂
     * @param arguments 数据参数
     * @param promise   是否需要写出应答对象
     * @return 如果promise为true返回写出应答对象, 如果promise为 false, 返回 null
     */
    <P extends RelayPacket<A>, A extends RelayPacketArguments> MessageWriteFuture write(
            RelayPacketFactory<P, A> factory, A arguments, boolean promise);

    /**
     * 发送转发数据包
     *
     * @param factory   数据包工厂
     * @param arguments 数据参数
     */
    default <P extends RelayPacket<A>, A extends RelayPacketArguments> void write(
            RelayPacketFactory<P, A> factory, A arguments) {
        write(factory, arguments, false);
    }

    /**
     * 转发消息到目标服务器
     *
     * @param from    tunnel
     * @param message 消息
     * @param awaiter 发送应答对象
     * @return 返回转发应答对象
     */
    MessageWriteFuture relay(RelayTunnel<?> from, Message message, MessageWriteFuture awaiter);

    /**
     * 转发消息到目标服务器
     *
     * @param from      tunnel
     * @param allocator 消息装配器
     * @param factory   消息工厂
     * @param context   消息上下文
     * @return 返回转发应答对象
     */
    MessageWriteFuture relay(RelayTunnel<?> from, MessageAllocator allocator, MessageFactory factory, MessageContent context);

    /**
     * 开启
     */
    void open();

    /**
     * 心跳
     */
    void heartbeat();

    /**
     * @return 最近一次心跳时间
     */
    long getLatelyHeartbeatTime();

    /**
     * ping
     */
    void ping();

    /**
     * pong
     */
    void pong();

}

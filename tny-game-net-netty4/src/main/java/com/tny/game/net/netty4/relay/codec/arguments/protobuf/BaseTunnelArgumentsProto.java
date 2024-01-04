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

package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
public abstract class BaseTunnelArgumentsProto<T extends TunnelPacketArguments> implements PacketArgumentsProto<T> {

    @Packed
    @Protobuf(order = 1, fieldType = FieldType.FIXED64)
    private long instanceId;

    @Packed
    @Protobuf(order = 2, fieldType = FieldType.FIXED64)
    private long tunnelId;

    protected BaseTunnelArgumentsProto() {
    }

    protected BaseTunnelArgumentsProto(T arguments) {
        this.instanceId = arguments.getInstanceId();
        this.tunnelId = arguments.getTunnelId();
    }

    public long getInstanceId() {
        return instanceId;
    }

    public long getTunnelId() {
        return tunnelId;
    }

    public BaseTunnelArgumentsProto<T> setTunnelId(long tunnelId) {
        this.tunnelId = tunnelId;
        return this;
    }

    public BaseTunnelArgumentsProto<T> setInstanceId(long instanceId) {
        this.instanceId = instanceId;
        return this;
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:23 下午
 */
public abstract class BaseRelayPacket<A extends RelayPacketArguments> implements RelayPacket<A> {

    private final int id;

    private final long time;

    private final RelayPacketType type;

    private final A arguments;

    public BaseRelayPacket(int id, RelayPacketType type, A arguments) {
        this.id = id;
        this.type = type;
        this.arguments = arguments;
        this.time = System.currentTimeMillis();
    }

    public BaseRelayPacket(int id, RelayPacketType type, long time, A arguments) {
        this.id = id;
        this.type = type;
        this.arguments = arguments;
        this.time = time;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public RelayPacketType getType() {
        return type;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public A getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", this.id)
                .append("type", this.type)
                .append("nanoTime", this.time)
                .toString();
    }

}

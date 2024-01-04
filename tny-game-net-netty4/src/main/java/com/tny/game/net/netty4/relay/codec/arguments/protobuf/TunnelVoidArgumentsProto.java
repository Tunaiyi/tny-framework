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

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
public class TunnelVoidArgumentsProto extends BaseTunnelArgumentsProto<TunnelVoidArguments> {

    public TunnelVoidArgumentsProto() {
    }

    public TunnelVoidArgumentsProto(TunnelVoidArguments arguments) {
        super(arguments);
    }

    @Override
    public TunnelVoidArguments toArguments() {
        return new TunnelVoidArguments(this.getInstanceId(), this.getTunnelId());
    }

}

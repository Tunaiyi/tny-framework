/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
@ProtobufClass
public class TunnelConnectArgumentsProto extends BaseTunnelArgumentsProto<TunnelConnectArguments> {

    @Packed
    @Protobuf(order = 10)
    private byte[] ipValue = new byte[4];

    @Protobuf(order = 11)
    private int port;

    public TunnelConnectArgumentsProto() {
    }

    public TunnelConnectArgumentsProto(TunnelConnectArguments arguments) {
        super(arguments);
        int[] ipValue = arguments.getIpValue();
        for (int i = 0; i < ipValue.length; i++) {
            this.ipValue[i] = (byte)ipValue[i];
        }
        this.port = arguments.getPort();
    }

    public byte[] getIpValue() {
        return ipValue;
    }

    public TunnelConnectArgumentsProto setIpValue(byte[] ipValue) {
        this.ipValue = ipValue;
        return this;
    }

    public int getPort() {
        return port;
    }

    public TunnelConnectArgumentsProto setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public TunnelConnectArguments toArguments() {
        return new TunnelConnectArguments(this.getInstanceId(), this.getTunnelId(), this.ipValue, this.port);
    }

}

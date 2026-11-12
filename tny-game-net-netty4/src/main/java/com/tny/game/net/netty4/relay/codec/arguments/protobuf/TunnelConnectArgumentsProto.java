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
            this.ipValue[i] = (byte) ipValue[i];
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

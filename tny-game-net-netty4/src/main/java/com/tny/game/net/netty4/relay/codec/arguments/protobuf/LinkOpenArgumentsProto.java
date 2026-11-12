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

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
@ProtobufClass
public class LinkOpenArgumentsProto extends BaseLinkArgumentsProto<LinkOpenArguments> {

    @Protobuf(order = 1)
    private String serveName;

    @Protobuf(order = 2, fieldType = FieldType.FIXED64)
    private long instance;

    @Protobuf(order = 3)
    private String key;

    @Protobuf(order = 4)
    private int serviceType;

    public LinkOpenArgumentsProto() {
    }

    public LinkOpenArgumentsProto(LinkOpenArguments arguments) {
        super(arguments);
        this.serveName = arguments.getService();
        this.serviceType = arguments.getServiceType().getId();
        this.instance = arguments.getInstance();
        this.key = arguments.getKey();
    }

    @Override
    public LinkOpenArguments toArguments() {
        return new LinkOpenArguments(RpcServiceTypes.check(serviceType), this.serveName, this.instance, this.key);
    }

    public String getServeName() {
        return serveName;
    }

    public int getServiceType() {
        return serviceType;
    }

    public long getInstance() {
        return instance;
    }

    public String getKey() {
        return key;
    }

}

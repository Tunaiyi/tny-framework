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

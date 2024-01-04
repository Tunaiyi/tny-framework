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

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
@ProtobufClass
public class LinkOpenedArgumentsProto extends BaseLinkArgumentsProto<LinkOpenedArguments> {

    @Protobuf(order = 1)
    private boolean result;

    public LinkOpenedArgumentsProto() {
        super();
    }

    public LinkOpenedArgumentsProto(LinkOpenedArguments arguments) {
        this.result = arguments.getResult();
    }

    @Override
    public LinkOpenedArguments toArguments() {
        return LinkOpenedArguments.of(this.result);
    }

    public boolean isResult() {
        return result;
    }

    public LinkOpenedArgumentsProto setResult(boolean result) {
        this.result = result;
        return this;
    }

}

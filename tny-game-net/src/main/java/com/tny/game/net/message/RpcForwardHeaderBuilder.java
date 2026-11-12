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
package com.tny.game.net.message;

import com.tny.game.net.application.*;

/**
 * Rpc转发HeaderBuilder
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 02:37
 **/
public class RpcForwardHeaderBuilder extends MessageHeaderBuilder<RpcForwardHeader> {

    private RpcForwardHeaderBuilder() {
    }

    public static RpcForwardHeaderBuilder newBuilder() {
        return new RpcForwardHeaderBuilder();
    }

    @Override
    protected RpcForwardHeader create() {
        return new RpcForwardHeader();
    }

    public RpcForwardHeaderBuilder setFrom(RpcServicer fromService) {
        header().setFrom(fromService);
        return this;
    }

    public RpcForwardHeaderBuilder setSender(Contact sender) {
        header().setSender(sender);
        return this;
    }

    public RpcForwardHeaderBuilder setTo(RpcServicer toServicer) {
        header().setTo(toServicer);
        return this;
    }

    public RpcForwardHeaderBuilder setReceiver(Contact receiver) {
        header().setReceiver(receiver);
        return this;
    }

    public RpcForwardHeaderBuilder setFromForwarder(RpcAccessPoint fromService) {
        // 原误调 setFrom/setTo：覆盖语义寻址字段并丢失转发者信息
        header().setFromForwarder(fromService);
        return this;
    }

    public RpcForwardHeaderBuilder setToForwarder(RpcAccessPoint toServicer) {
        header().setToForwarder(toServicer);
        return this;
    }

}

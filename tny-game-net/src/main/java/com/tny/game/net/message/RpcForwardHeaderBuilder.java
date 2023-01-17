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
package com.tny.game.net.message;

import com.tny.game.net.base.*;

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

    public RpcForwardHeaderBuilder setSender(Messager sender) {
        header().setSender(sender);
        return this;
    }

    public RpcForwardHeaderBuilder setTo(RpcServicer toServicer) {
        header().setTo(toServicer);
        return this;
    }

    public RpcForwardHeaderBuilder setReceiver(Messager receiver) {
        header().setReceiver(receiver);
        return this;
    }

    public RpcForwardHeaderBuilder setFromForwarder(RpcAccessPoint fromService) {
        header().setFrom(fromService);
        return this;
    }

    public RpcForwardHeaderBuilder setToForwarder(RpcAccessPoint toServicer) {
        header().setTo(toServicer);
        return this;
    }

}

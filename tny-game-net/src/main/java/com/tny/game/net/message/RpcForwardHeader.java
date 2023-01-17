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

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.net.base.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Rpc消息头附件
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 02:37
 **/

@TypeProtobuf(MessageHeaderConstants.RPC_FORWARD_HEADER_TYPE_PROTO)
@Codable(TypeProtobufMimeType.TYPE_PROTOBUF)
@ProtobufClass
public class RpcForwardHeader extends MessageHeader<RpcForwardHeader> {

    /**
     * 请求服务
     */
    @Protobuf(order = 2)
    private ForwardPoint from;

    /**
     * 发送者
     */
    @Protobuf(order = 3)
    private ForwardMessager sender;

    /**
     * 目标服务
     */
    @Protobuf(order = 4)
    private ForwardPoint to;

    /**
     * 目标接受者
     */
    @Protobuf(order = 5)
    private ForwardMessager receiver;

    /**
     * 请求转发者
     */
    @Packed
    @Protobuf(order = 6)
    private ForwardPoint fromForwarder;

    /**
     * 目标转发者
     */
    @Packed
    @Protobuf(order = 7)
    private ForwardPoint toForwarder;

    public RpcForwardHeader() {
    }

    public ForwardPoint getFrom() {
        return from;
    }

    public ForwardMessager getSender() {
        return sender;
    }

    public ForwardPoint getTo() {
        return to;
    }

    public ForwardMessager getReceiver() {
        return receiver;
    }

    public ForwardPoint getFromForwarder() {
        return fromForwarder;
    }

    public ForwardPoint getToForwarder() {
        return toForwarder;
    }

    @Override
    public String getKey() {
        return MessageHeaderConstants.RPC_FORWARD_HEADER_KEY;
    }

    @Override
    public boolean isTransitive() {
        return false;
    }

    protected RpcForwardHeader setFrom(RpcServicer fromService) {
        this.from = toForwardPoint(fromService);
        return this;
    }

    protected RpcForwardHeader setSender(Messager sender) {
        this.sender = toForwardMessager(sender);
        return this;
    }

    protected RpcForwardHeader setTo(RpcServicer toServicer) {
        this.to = toForwardPoint(toServicer);
        return this;
    }

    protected RpcForwardHeader setReceiver(Messager receiver) {
        this.receiver = toForwardMessager(receiver);
        return this;
    }

    protected RpcForwardHeader setFromForwarder(RpcServicer fromService) {
        this.fromForwarder = toForwardPoint(fromForwarder);
        return this;
    }

    protected RpcForwardHeader setToForwarder(RpcServicer fromService) {
        this.toForwarder = toForwardPoint(toForwarder);
        return this;
    }

    private ForwardPoint toForwardPoint(RpcServicer rpcServicer) {
        if (rpcServicer == null) {
            return null;
        }
        if (rpcServicer instanceof ForwardPoint) {
            return as(rpcServicer);
        } else {
            return new ForwardPoint(rpcServicer);
        }
    }

    private ForwardMessager toForwardMessager(Messager messager) {
        if (messager == null) {
            return null;
        }
        if (messager instanceof ForwardMessager) {
            return as(messager);
        } else {
            return new ForwardMessager(messager);
        }
    }

}

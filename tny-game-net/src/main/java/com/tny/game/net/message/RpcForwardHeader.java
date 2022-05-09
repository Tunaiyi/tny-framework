package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.net.base.*;

import java.util.*;

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

    @Protobuf(order = 2)
    private ForwardRpcServicer from;

    @Protobuf(order = 3)
    private ForwardMessager sender;

    @Protobuf(order = 4)
    private ForwardRpcServicer to;

    @Protobuf(order = 5)
    private ForwardMessager receiver;

    @Packed
    @Protobuf(order = 6)
    private List<ForwardRpcServicer> forwarders = new ArrayList<>();

    public RpcForwardHeader() {
    }

    public ForwardRpcServicer getFrom() {
        return from;
    }

    public ForwardMessager getSender() {
        return sender;
    }

    public List<RpcServicer> getForwarders() {
        return Collections.unmodifiableList(forwarders);
    }

    public ForwardRpcServicer getTo() {
        return to;
    }

    public ForwardMessager getReceiver() {
        return receiver;
    }

    @Override
    public String getKey() {
        return MessageHeaderConstants.RPC_FORWARD_HEADER_KEY;
    }

    protected RpcForwardHeader setFrom(RpcServicer fromService) {
        this.from = toForwardRpcServicer(fromService);
        return this;
    }

    protected RpcForwardHeader setSender(Messager sender) {
        this.sender = toForwardMessager(sender);
        return this;
    }

    protected RpcForwardHeader setTo(RpcServicer toServicer) {
        this.to = toForwardRpcServicer(toServicer);
        return this;
    }

    protected RpcForwardHeader setReceiver(Messager receiver) {
        this.receiver = toForwardMessager(receiver);
        return this;
    }

    protected RpcForwardHeader setForwarders(List<ForwardRpcServicer> forwarders) {
        this.forwarders = forwarders;
        return this;
    }

    private ForwardRpcServicer toForwardRpcServicer(RpcServicer rpcServicer) {
        if (rpcServicer == null) {
            return null;
        }
        if (rpcServicer instanceof ForwardRpcServicer) {
            return as(rpcServicer);
        } else {
            return new ForwardRpcServicer(rpcServicer);
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

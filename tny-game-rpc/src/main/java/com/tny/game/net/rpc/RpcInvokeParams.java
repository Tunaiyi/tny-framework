package com.tny.game.net.rpc;

import com.tny.game.common.collection.empty.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 调用参数
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/2 04:29
 **/
public class RpcInvokeParams {

    private Object[] params;

    private RpcServicer from;

    private Messager sender;

    private RpcServicer to;

    private Messager receiver;

    private Object routeValue;

    private ResultCode code = NetResultCode.SUCCESS;

    private final Map<String, MessageHeader<?>> headerMap = new EmptyImmutableMap<>();

    public RpcInvokeParams(int size) {
        this.params = new Object[size];
    }

    public Object[] getParams() {
        return params;
    }

    public Collection<MessageHeader<?>> getAllHeaders() {
        if (!headerMap.containsKey(MessageHeaderConstants.RPC_FORWARD_HEADER_KEY)) {
            if (ObjectAide.isAnyExist(to, from, receiver, sender)) {
                RpcForwardHeader forwardHeader = RpcForwardHeaderBuilder.newBuilder()
                        .setFrom(from)
                        .setTo(to)
                        .setSender(sender)
                        .setReceiver(receiver)
                        .build();
                headerMap.put(forwardHeader.getKey(), forwardHeader);
            }
        }
        return headerMap.values();
    }

    public void setParams(int index, Object value) {
        if (this.params[index] == null) {
            this.params[index] = value;
        } else {
            throw new IllegalArgumentException(format("参数 {} 已设置", index));
        }
    }

    public Object getBody() {
        if (params.length == 0) {
            return null;
        }
        return params[0];
    }

    public RpcServicer getFrom() {
        return from;
    }

    public Messager getSender() {
        return sender;
    }

    public RpcServicer getTo() {
        return to;
    }

    public Messager getReceiver() {
        return receiver;
    }

    public Object getRouteValue() {
        return routeValue;
    }

    public ResultCode getCode() {
        return code;
    }

    RpcInvokeParams setParams(Object[] params) {
        this.params = params;
        return this;
    }

    RpcInvokeParams setFrom(RpcServicer from) {
        this.from = from;
        return this;
    }

    RpcInvokeParams setSender(Messager sender) {
        this.sender = sender;
        return this;
    }

    RpcInvokeParams setTo(RpcServicer to) {
        this.to = to;
        return this;
    }

    RpcInvokeParams setTo(RpcServiceType toService) {
        this.to = new ForwardRpcServicer(toService);
        return this;
    }

    RpcInvokeParams setReceiver(Messager receiver) {
        this.receiver = receiver;
        return this;
    }

    RpcInvokeParams setRouteValue(Object routeValue) {
        this.routeValue = routeValue;
        return this;
    }

    RpcInvokeParams putHeader(MessageHeader<?> messageHeader) {
        this.headerMap.put(messageHeader.getKey(), messageHeader);
        return this;
    }

    RpcInvokeParams setCode(Object code) {
        if (code instanceof Number) {
            this.code = ResultCodes.of(((Number)code).intValue());
        } else if (code instanceof ResultCode) {
            this.code = as(code);
        } else {
            throw new ClassCastException(format("{}类型参数 {},无法解析位 {}", code.getClass(), code, ResultCode.class));
        }
        return this;
    }

    RpcInvokeParams setBody(Object body) {
        this.setParams(0, body);
        return this;
    }

}

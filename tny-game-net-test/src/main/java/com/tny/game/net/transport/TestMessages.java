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
package com.tny.game.net.transport;

import com.tny.game.common.result.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/24.
 */
public class TestMessages {

    private List<TestMessagePack> messages = new ArrayList<>();

    private MessageFactory messageBuilderFactory = new CommonMessageFactory();

    private Certificate<Long> certificate;

    private AtomicInteger idCreator;

    private int pingSize = 0;

    private int pongSize = 0;

    private int messageSize = 0;

    private int pushSize = 0;

    private int requestSize = 0;

    private int responseSize = 0;

    private int protocol = 1000;

    public TestMessages(NetTunnel<Long> tunnel) {
        this.certificate = tunnel.getCertificate();
        this.idCreator = new AtomicInteger(0);
    }

    public TestMessages(NetEndpoint<Long> endpoint) {
        this.certificate = endpoint.getCertificate();
        this.idCreator = new AtomicInteger(0);
    }

    public List<? extends Message> getMessages() {
        return this.messages.stream().map(TestMessagePack::getMessage).collect(Collectors.toList());
    }

    public List<? extends Message> getMessages(MessageMode... modes) {
        List<MessageMode> messageModes = Arrays.asList(modes);
        return this.messages.stream()
                .map(TestMessagePack::getMessage)
                .filter(m -> messageModes.contains(m.getMode()))
                .collect(Collectors.toList());
    }

    public List<MessageSubject> getSubject() {
        return this.messages.stream().map(TestMessagePack::getContext).collect(Collectors.toList());
    }

    public TestMessages addPush(Object body) {
        return addPush(ResultCode.SUCCESS, body);
    }

    public TestMessages addPush(ResultCode code, Object body) {
        return addMessageContext(MessageContents.push(Protocols.protocol(this.protocol), code, body));
    }

    public TestMessages addRequest(Object body) {
        return addMessageContext(MessageContents.request(Protocols.protocol(this.protocol), body));
    }

    public TestMessages addResponse(Object body, long toMessage) {
        return addMessageContext(MessageContents.respond(Protocols.protocol(this.protocol), body, toMessage));
    }

    public TestMessages addResponse(ResultCode code, Object body, long toMessage) {
        return addMessageContext(MessageContents.respond(Protocols.protocol(this.protocol), code, body, toMessage));
    }

    public TestMessages addPing() {
        return addDetectMessage(TickMessage.ping());
    }

    public TestMessages addPong() {
        return addDetectMessage(TickMessage.pong());
    }

    private TestMessages addMessagePack(TestMessagePack pack) {
        this.messages.add(pack);
        switch (pack.getMode()) {
            case PUSH:
                this.messageSize++;
                this.pushSize++;
                break;
            case REQUEST:
                this.messageSize++;
                this.requestSize++;
                break;
            case RESPONSE:
                this.messageSize++;
                this.responseSize++;
                break;
            case PING:
                this.pingSize++;
                break;
            case PONG:
                this.pongSize++;
                break;
        }
        return this;
    }

    public TestMessages addDetectMessage(NetMessage message) {
        return this.addMessagePack(new TestMessagePack(null, message));
    }

    public TestMessages addMessageContext(MessageContent context) {
        return this.addMessagePack(new TestMessagePack(context, createMessage(context)));
    }

    private NetMessage createMessage(MessageContent context) {
        return this.messageBuilderFactory.create(this.idCreator.incrementAndGet(), context);
    }

    public int getPingSize() {
        return this.pingSize;
    }

    public int getPongSize() {
        return this.pongSize;
    }

    public int getMessageSize() {
        return this.messageSize;
    }

    public int getPushSize() {
        return this.pushSize;
    }

    public int getRequestSize() {
        return this.requestSize;
    }

    public int getResponseSize() {
        return this.responseSize;
    }

    public void receive(NetTunnel<?> receiver) {
        assertFalse(this.messages.isEmpty());
        this.messages.forEach(p -> receiver.receive(RpcProviderContext.create(receiver, (NetMessage)p)));
    }

    public void send(Sender sender) {
        send(sender, null);
    }

    public void send(Sender sender, Consumer<SendReceipt> check) {
        assertFalse(this.messages.isEmpty());
        if (check == null) {
            check = f -> {
            };
        }
        Consumer<SendReceipt> consumer = check;
        this.messages.forEach(p -> consumer.accept(sender.send(p.getContext())));
    }

    public void write(Transport transport) {
        write(transport, null);
    }

    public void write(Transport transport, Consumer<MessageWriteFuture> check) {
        assertFalse(this.messages.isEmpty());
        if (check == null) {
            check = f -> {
            };
        }
        final Consumer<MessageWriteFuture> consumer = check;
        this.messages.forEach(p -> consumer.accept(transport.write(p.getMessage(), null)));
    }
    // public void sendSync(Sender<Long> sender, long timeout) {
    //     assertFalse(this.messages.isEmpty());
    //     this.messages.forEach(p -> sender.sendSync(p.getContext(), timeout));
    // }

    // public void sendSuccess(NetTunnel<Long> tunnel) {
    //     this.forEach((c, m) -> {
    //         MessageSendFuture<Long> sendFuture = c.getSendFuture();
    //         if (sendFuture != null)
    //             sendFuture.complete(m);
    //         RespondFuture<Long> respondFuture = c.getRespondFuture();
    //         if (respondFuture != null)
    //             tunnel.registerFuture(m.getId(), respondFuture);
    //     });
    // }

    // public void sendFailed(Throwable cause) {
    //     this.forEach((p) -> {
    //         MessageContext context = p.getContext();
    //         if (context == null)
    //             return;
    //         MessageSendFuture<Long> future = context.getSendFuture();
    //         if (future != null)
    //             future.completeExceptionally(cause);
    //         RespondFuture<Long> respondFuture = context.getRespondFuture();
    //         if (respondFuture != null)
    //             respondFuture.completeExceptionally(cause);
    //     });
    // }

    public void forEach(Consumer<TestMessagePack> action) {
        this.messages.forEach(action);
    }

    public void messagesForEach(Consumer<NetMessage> action) {
        this.messages.stream().map(TestMessagePack::getMessage).forEach(action);
    }

    public void contextsForEach(Consumer<MessageContent> action) {
        this.messages.stream().map(TestMessagePack::getContext).forEach(action);
    }

    public void requestContextsForEach(Consumer<RequestContent> action) {
        this.messages.stream().map(TestMessagePack::getRequestContext).forEach(action);
    }

    public static TestMessages createMessages(NetTunnel<Long> tunnel) {
        return createMessages(new TestMessages(tunnel));
    }

    public static TestMessages createMessages(NetEndpoint<Long> endpoint) {
        return createMessages(new TestMessages(endpoint));
    }

    private static TestMessages createMessages(TestMessages messages) {
        return messages.addPush("push 1")
                .addResponse("request 2", 1)
                .addResponse("request 3", 1)
                .addResponse("request 4", 1)
                .addResponse("request 5", 1)
                .addResponse("request 6", 1)
                .addRequest("request 7");
    }

}

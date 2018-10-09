package com.tny.game.net.transport;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.transport.message.*;
import com.tny.game.net.transport.message.common.CommonMessageFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/24.
 */
public class TestMessages {

    private List<TestMessagePack> messages = new ArrayList<>();

    private MessageFactory<Long> messageBuilderFactory = new CommonMessageFactory<>();
    private Certificate<Long> certificate;
    private int id = 0;
    private int pingSize = 0;
    private int pongSize = 0;
    private int messageSize = 0;
    private int pushSize = 0;
    private int requestSize = 0;
    private int responseSize = 0;
    private int protocol = 1000;

    public TestMessages(NetTunnel<Long> tunnel) {
        this.certificate = tunnel.getCertificate();
    }

    TestMessages(NetSession<Long> session) {
        this.certificate = session.getCertificate();
    }

    public List<? extends Message<Long>> getMessages() {
        return messages.stream().map(TestMessagePack::getMessage).collect(Collectors.toList());
    }

    public List<? extends Message<Long>> getMessages(MessageMode... modes) {
        List<MessageMode> messageModes = Arrays.asList(modes);
        return messages.stream()
                .map(TestMessagePack::getMessage)
                .filter(m -> messageModes.contains(m.getMode()))
                .collect(Collectors.toList());
    }

    public List<MessageSubject> getSubject() {
        return messages.stream().map(TestMessagePack::getSubject).collect(Collectors.toList());
    }

    public TestMessages addPush(Object body) {
        return addPush(ResultCode.SUCCESS, body);
    }

    public TestMessages addPush(ResultCode code, Object body) {
        MessageSubject subject = MessageSubjectBuilder.pushBuilder(ProtocolAide.protocol(protocol), code)
                .setBody(body)
                .build();
        return addMessageSubject(subject);
    }

    public TestMessages addRequest(Object body) {
        MessageSubject subject = MessageSubjectBuilder.requestBuilder(ProtocolAide.protocol(protocol))
                .setBody(body)
                .build();
        return addMessageSubject(subject);
    }

    public TestMessages addResponse(Object body, long toMessage) {
        MessageSubject subject = MessageSubjectBuilder.respondBuilder(ProtocolAide.protocol(protocol), ResultCode.SUCCESS, toMessage)
                .setBody(body)
                .build();
        return addMessageSubject(subject);
    }

    public TestMessages addResponse(ResultCode code, Object body, long toMessage) {
        MessageSubject subject = MessageSubjectBuilder.respondBuilder(ProtocolAide.protocol(protocol), code, toMessage)
                .setBody(body)
                .build();
        return addMessageSubject(subject);
    }

    public TestMessages addPing() {
        return addDetectMessage(DetectMessage.ping());
    }

    public TestMessages addPong() {
        return addDetectMessage(DetectMessage.pong());
    }

    private TestMessages addMessagePack(TestMessagePack pack) {
        this.messages.add(pack);
        switch (pack.getMode()) {
            case PUSH:
                messageSize++;
                pushSize++;
                break;
            case REQUEST:
                messageSize++;
                requestSize++;
                break;
            case RESPONSE:
                messageSize++;
                responseSize++;
                break;
            case PING:
                pingSize++;
                break;
            case PONG:
                pongSize++;
                break;
        }
        return this;
    }

    public TestMessages addDetectMessage(NetMessage<Long> message) {
        return this.addMessagePack(new TestMessagePack(null, message));
    }


    public TestMessages addMessageSubject(MessageSubject subject) {
        return this.addMessagePack(new TestMessagePack(subject, createMessage(subject)));
    }

    private NetMessage<Long> createMessage(MessageSubject subject) {
        return messageBuilderFactory.create(++id, subject, certificate);
    }

    public int getPingSize() {
        return pingSize;
    }

    public int getPongSize() {
        return pongSize;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public int getPushSize() {
        return pushSize;
    }

    public int getRequestSize() {
        return requestSize;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public void receive(NetTunnel<Long> tunnel) {
        assertFalse(this.messages.isEmpty());
        this.messages.stream().map(TestMessagePack::getMessage).forEach(tunnel::receive);
    }

    public void send(Communicator<Long> communicator) {
        assertFalse(this.messages.isEmpty());
        this.messages.forEach(p -> communicator.sendAsyn(p.getSubject(), p.getContext()));
    }


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

    public void sendFailed(Throwable cause) {
        this.forEach((p) -> {
            MessageContext context = p.getContext();
            if (context == null)
                return;
            MessageSendFuture<Long> future = context.getSendFuture();
            if (future != null)
                future.completeExceptionally(cause);
            RespondFuture<Long> respondFuture = context.getRespondFuture();
            if (respondFuture != null)
                respondFuture.completeExceptionally(cause);
        });
    }

    public void forEach(Consumer<TestMessagePack> action) {
        this.messages.forEach(action);
    }

    public void subjcetForEach(Consumer<MessageSubject> action) {
        this.messages.stream().map(TestMessagePack::getSubject).forEach(action);
    }

    public void messagesForEach(Consumer<NetMessage<Long>> action) {
        this.messages.stream().map(TestMessagePack::getMessage).forEach(action);
    }

    public void contentsForEach(Consumer<MessageContext<Long>> action) {
        this.messages.stream().map(TestMessagePack::context).forEach(action);
    }

}

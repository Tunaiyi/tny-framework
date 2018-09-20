package com.tny.game.net.transport;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.transport.message.*;
import com.tny.game.net.transport.message.common.CommonMessageBuilderFactory;

import java.util.*;
import java.util.function.*;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/24.
 */
public class TestMessages {

    private List<Message<Long>> messages = new ArrayList<>();
    private List<MessageContext<Long>> contents = new ArrayList<>();
    private MessageBuilderFactory<Long> messageBuilderFactory = new CommonMessageBuilderFactory<>();

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

    public List<Message<Long>> getMessages() {
        return messages;
    }

    public List<MessageContext<Long>> getContents() {
        return contents;
    }

    public TestMessages addPush(Object message) {
        return addPush(ResultCode.SUCCESS, message);
    }

    public TestMessages addPush(ResultCode code, Object message) {
        MessageContext<Long> content = MessageContext.toPush(ProtocolAide.protocol(protocol), code, message);
        return addMessage(content);
    }

    public TestMessages addRequest(Object message) {
        MessageContext<Long> content = MessageContext.toRequest(ProtocolAide.protocol(protocol), message);
        return addMessage(content);
    }

    public TestMessages addResponse(Object message, long toMessage) {
        MessageContext<Long> content = MessageContext.toResponse(ProtocolAide.protocol(protocol), ResultCode.SUCCESS, message, toMessage);
        return addMessage(content);
    }

    public TestMessages addResponse(ResultCode code, Object message, long toMessage) {
        MessageContext<Long> content = MessageContext.toResponse(ProtocolAide.protocol(protocol), code, message, toMessage);
        return addMessage(content);
    }

    public TestMessages addPing() {
        return addMessage(DetectMessage.ping());
    }

    public TestMessages addPong() {
        return addMessage(DetectMessage.pong());
    }

    public Message<Long> createPushMessage(Object message) {
        return createPushMessage(ResultCode.SUCCESS, message);
    }

    public Message<Long> createPushMessage(ResultCode code, Object message) {
        MessageContext<Long> content = MessageContext.toPush(ProtocolAide.protocol(protocol), code, message);
        return createMessage(content);
    }

    public Message<Long> createRequestMessage(Object message) {
        MessageContext<Long> content = MessageContext.toRequest(ProtocolAide.protocol(protocol), message);
        return createMessage(content);
    }

    public Message<Long> createResponseMessage(Object message, long toMessage) {
        MessageContext<Long> content = MessageContext.toResponse(ProtocolAide.protocol(protocol), ResultCode.SUCCESS, message, toMessage);
        return createMessage(content);
    }

    public Message<Long> createResponseMessage(ResultCode code, Object message, long toMessage) {
        MessageContext<Long> content = MessageContext.toResponse(ProtocolAide.protocol(protocol), code, message, toMessage);
        return createMessage(content);
    }


    public MessageContext<Long> createPushContent(Object message) {
        return createPushContent(ResultCode.SUCCESS, message);
    }

    public MessageContext<Long> createPushContent(ResultCode code, Object message) {
        return MessageContext.toPush(ProtocolAide.protocol(protocol), code, message);
    }

    public MessageContext<Long> createRequestContent(Object message) {
        return MessageContext.toRequest(ProtocolAide.protocol(protocol), message);
    }

    public MessageContext<Long> createResponseContent(Object message, long toMessage) {
        return MessageContext.toResponse(ProtocolAide.protocol(protocol), ResultCode.SUCCESS, message, toMessage);
    }

    public MessageContext<Long> createResponseContent(ResultCode code, Object message, long toMessage) {
        return MessageContext.toResponse(ProtocolAide.protocol(protocol), code, message, toMessage);
    }

    public TestMessages addMessage(Message<Long> message) {
        this.messages.add(message);
        switch (message.getMode()) {
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

    public TestMessages addMessage(MessageContext<Long> content) {
        this.contents.add(content);
        return this.addMessage(createMessage(content));
    }

    private Message<Long> createMessage(MessageContext<Long> content) {
        return messageBuilderFactory.newBuilder()
                .setId(++id)
                .setContent(content)
                .setCertificate(certificate)
                .build();
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
        this.messages.forEach(tunnel::receive);
    }

    public void send(NetTunnel<Long> tunnel) {
        assertFalse(this.contents.isEmpty());
        this.contents.forEach(tunnel::send);
    }

    public void send(NetSession<Long> session) {
        assertFalse(this.contents.isEmpty());
        this.contents.forEach(session::send);
    }

    public void forEach(BiConsumer<MessageContext<Long>, Message<Long>> action) {
        int size = this.messages.size();
        for (int index = 0; index < size; index++) {
            action.accept(this.contents.get(index), this.messages.get(index));
        }
    }

    public void sendSuccess(NetTunnel<Long> tunnel) {
        this.forEach((c, m) -> c.sendSuccess(tunnel, m));
    }

    public void sendFailed(Throwable cause) {
        this.forEach((c, m) -> c.sendFailed(cause));
    }

    public void messagesForEach(Consumer<Message<Long>> action) {
        this.messages.forEach(action);
    }

    public void contentsForEach(Consumer<MessageContext<Long>> action) {
        this.contents.forEach(action);
    }

}

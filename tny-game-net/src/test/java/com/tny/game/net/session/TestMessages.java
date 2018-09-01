package com.tny.game.net.session;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.CommonMessageBuilderFactory;
import com.tny.game.net.tunnel.NetTunnel;

import java.util.*;
import java.util.function.*;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/24.
 */
public class TestMessages {

    private List<Message<Long>> messages = new ArrayList<>();
    private List<MessageContent<Long>> contents = new ArrayList<>();
    private MessageBuilderFactory<Long> messageBuilderFactory = new CommonMessageBuilderFactory<>();

    private NetTunnel<Long> tunnel;
    private int id = 0;
    private int pingSize = 0;
    private int pongSize = 0;
    private int messageSize = 0;
    private int pushSize = 0;
    private int requestSize = 0;
    private int responseSize = 0;
    private int protocol = 1000;

    public TestMessages(NetTunnel<Long> tunnel) {
        this.tunnel = tunnel;
    }

    TestMessages(NetSession<Long> session) {
        this.tunnel = session.getCurrentTunnel();
    }

    public List<Message<Long>> getMessages() {
        return messages;
    }

    public List<MessageContent<Long>> getContents() {
        return contents;
    }

    public TestMessages addPush(Object message) {
        return addPush(ResultCode.SUCCESS, message);
    }

    public TestMessages addPush(ResultCode code, Object message) {
        MessageContent<Long> content = MessageContent.toPush(ProtocolAide.protocol(protocol), code, message);
        return addMessage(content);
    }

    public TestMessages addRequest(Object message) {
        MessageContent<Long> content = MessageContent.toRequest(ProtocolAide.protocol(protocol), message);
        return addMessage(content);
    }

    public TestMessages addResponse(Object message, int toMessage) {
        MessageContent<Long> content = MessageContent.toResponse(ProtocolAide.protocol(protocol), ResultCode.SUCCESS, message, toMessage);
        return addMessage(content);
    }

    public TestMessages addResponse(ResultCode code, Object message, int toMessage) {
        MessageContent<Long> content = MessageContent.toResponse(ProtocolAide.protocol(protocol), code, message, toMessage);
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
        MessageContent<Long> content = MessageContent.toPush(ProtocolAide.protocol(protocol), code, message);
        return createMessage(content);
    }

    public Message<Long> createRequestMessage(Object message) {
        MessageContent<Long> content = MessageContent.toRequest(ProtocolAide.protocol(protocol), message);
        return createMessage(content);
    }

    public Message<Long> createResponseMessage(Object message, int toMessage) {
        MessageContent<Long> content = MessageContent.toResponse(ProtocolAide.protocol(protocol), ResultCode.SUCCESS, message, toMessage);
        return createMessage(content);
    }

    public Message<Long> createResponseMessage(ResultCode code, Object message, int toMessage) {
        MessageContent<Long> content = MessageContent.toResponse(ProtocolAide.protocol(protocol), code, message, toMessage);
        return createMessage(content);
    }


    public MessageContent<Long> createPushContent(Object message) {
        return createPushContent(ResultCode.SUCCESS, message);
    }

    public MessageContent<Long> createPushContent(ResultCode code, Object message) {
        return MessageContent.toPush(ProtocolAide.protocol(protocol), code, message);
    }

    public MessageContent<Long> createRequestContent(Object message) {
        return MessageContent.toRequest(ProtocolAide.protocol(protocol), message);
    }

    public MessageContent<Long> createResponseContent(Object message, int toMessage) {
        return MessageContent.toResponse(ProtocolAide.protocol(protocol), ResultCode.SUCCESS, message, toMessage);
    }

    public MessageContent<Long> createResponseContent(ResultCode code, Object message, int toMessage) {
        return MessageContent.toResponse(ProtocolAide.protocol(protocol), code, message, toMessage);
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

    public TestMessages addMessage(MessageContent<Long> content) {
        this.contents.add(content);
        return this.addMessage(createMessage(content));
    }

    private Message<Long> createMessage(MessageContent<Long> content) {
        return messageBuilderFactory.newBuilder()
                .setID(++id)
                .setContent(content)
                .setTunnel(tunnel)
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

    public void receive(NetSession<Long> session) {
        assertFalse(this.messages.isEmpty());
        this.messages.forEach(session::receive);
    }

    public void send(NetSession<Long> session) {
        assertFalse(this.contents.isEmpty());
        this.contents.forEach(session::send);
    }

    public void forEach(BiConsumer<MessageContent<Long>, Message<Long>> action) {
        int size = this.messages.size();
        for (int index = 0; index < size; index++) {
            action.accept(this.contents.get(index), this.messages.get(index));
        }
    }

    public void messagesForEach(Consumer<Message<Long>> action) {
        this.messages.forEach(action);
    }

    public void contentsForEach(Consumer<MessageContent<Long>> action) {
        this.contents.forEach(action);
    }

}

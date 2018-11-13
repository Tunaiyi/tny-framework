package com.tny.game.net.transport;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.CommonMessageFactory;
import com.tny.game.net.endpoint.NetSession;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/24.
 */
public class TestMessages {

    private List<TestMessagePack> messages = new ArrayList<>();

    private MessageFactory<Long> messageBuilderFactory = new CommonMessageFactory<>();
    private Certificate<Long> certificate;
    private MessageIdCreator idCreator;
    private int pingSize = 0;
    private int pongSize = 0;
    private int messageSize = 0;
    private int pushSize = 0;
    private int requestSize = 0;
    private int responseSize = 0;
    private int protocol = 1000;

    public TestMessages(NetTunnel<Long> tunnel) {
        this.certificate = tunnel.getCertificate();
        idCreator = new MessageIdCreator(MessageIdCreator.TUNNEL_SENDER_MESSAGE_ID_MARK);
    }

    TestMessages(NetSession<Long> session) {
        this.certificate = session.getCertificate();
        idCreator = new MessageIdCreator(MessageIdCreator.ENDPOINT_SENDER_MESSAGE_ID_MARK);
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
        return messages.stream().map(TestMessagePack::getContext).collect(Collectors.toList());
    }

    public TestMessages addPush(Object body) {
        return addPush(ResultCode.SUCCESS, body);
    }

    public TestMessages addPush(ResultCode code, Object body) {
        return addMessageContext(MessageContexts.push(ProtocolAide.protocol(protocol), code, body));
    }

    public TestMessages addRequest(Object body) {
        return addMessageContext(MessageContexts.request(ProtocolAide.protocol(protocol), body));
    }

    public TestMessages addResponse(Object body, long toMessage) {
        return addMessageContext(MessageContexts.respond(ProtocolAide.protocol(protocol), body, toMessage));
    }

    public TestMessages addResponse(ResultCode code, Object body, long toMessage) {
        return addMessageContext(MessageContexts.respond(ProtocolAide.protocol(protocol), code, body, toMessage));
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


    public TestMessages addMessageContext(MessageContext<Long> context) {
        return this.addMessagePack(new TestMessagePack(context, createMessage(context)));
    }

    private NetMessage<Long> createMessage(MessageContext<Long> context) {
        return messageBuilderFactory.create(idCreator.createId(), context, certificate);
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

    public List<Boolean> receive(NetTunnel<Long> tunnel) {
        return this.receive(tunnel, null);
    }

    public List<Boolean> receive(NetTunnel<Long> tunnel, BiConsumer<Message<Long>, Boolean> consumer) {
        assertFalse(this.messages.isEmpty());
        return this.messages.stream().map(TestMessagePack::getMessage)
                .map(m -> {
                    boolean result = tunnel.receive(m);
                    if (consumer != null)
                        consumer.accept(m, result);
                    return result;
                })
                .collect(Collectors.toList());
    }

    public void sendAsyn(Sender<Long> sender) {
        assertFalse(this.messages.isEmpty());
        this.messages.forEach(p -> sender.sendAsyn(p.getContext()));
    }

    public void sendSync(Sender<Long> sender, long timeout) {
        assertFalse(this.messages.isEmpty());
        this.messages.forEach(p -> sender.sendSync(p.getContext(), timeout));
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

    public void messagesForEach(Consumer<NetMessage<Long>> action) {
        this.messages.stream().map(TestMessagePack::getMessage).forEach(action);
    }

    public void contextsForEach(Consumer<MessageContext<Long>> action) {
        this.messages.stream().map(TestMessagePack::getContext).forEach(action);
    }

    public void requestContextsForEach(Consumer<RequestContext<Long>> action) {
        this.messages.stream().map(TestMessagePack::getRequestContext).forEach(action);
    }


}

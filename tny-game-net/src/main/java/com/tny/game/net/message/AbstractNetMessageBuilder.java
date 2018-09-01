package com.tny.game.net.message;

import com.tny.game.net.command.CommandResult;
import com.tny.game.net.session.MessageContent;
import com.tny.game.net.tunnel.Tunnel;

import java.util.function.Supplier;

/**
 * 抽象MessageBuilder
 * Created by Kun Yang on 2017/3/21.
 */
public abstract class AbstractNetMessageBuilder<UID, M extends AbstractNetMessage<UID>> implements MessageBuilder<UID> {

    protected int id;

    private long sessionID;

    private int protocol = -1;

    private int code;

    private Object body;

    private int toMessage;

    protected Tunnel<UID> tunnel;

    private Supplier<M> messageCreator;

    private Object head;

    private Supplier<? extends AbstractNetMessageHeader> headCreator;

    protected AbstractNetMessageBuilder(Supplier<M> messageCreator, Supplier<? extends AbstractNetMessageHeader> headCreator) {
        this.messageCreator = messageCreator;
        this.headCreator = headCreator;
    }

    @Override
    public MessageBuilder<UID> setID(int id) {
        this.id = id;
        return this;
    }

    @Override
    public MessageBuilder<UID> setSessionID(long sessionID) {
        this.sessionID = sessionID;
        return this;
    }

    @Override
    public MessageBuilder<UID> setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public MessageBuilder<UID> setBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public MessageBuilder<UID> setToMessage(int toMessageID) {
        this.toMessage = toMessageID;
        return this;
    }

    @Override
    public MessageBuilder<UID> setTunnel(Tunnel<UID> tunnel) {
        this.tunnel = tunnel;
        return this;
    }

    @Override
    public MessageBuilder<UID> setProtocol(Protocol protocol) {
        if (this.protocol <= 0)
            this.protocol = protocol.getProtocol();
        return this;
    }

    protected MessageBuilder<UID> setHead(Object head) {
        this.head = head;
        return this;
    }

    @Override
    public MessageBuilder<UID> setContent(MessageContent content) {
        if (this.protocol <= 0)
            this.protocol = content.getProtocol();
        this.code = content.getCode().getCode();
        this.body = content.getBody();
        this.toMessage = content.getToMessage();
        return this;
    }

    @Override
    public MessageBuilder<UID> setCommandResult(CommandResult result) {
        Protocol protocol = result.getProtocol();
        if (protocol != null)
            this.protocol = protocol.getProtocol();
        this.code = result.getResultCode().getCode();
        this.body = result.getBody();
        return this;
    }

    @Override
    public Message<UID> build() {
        M message = this.messageCreator.get();
        if (this.protocol == 0)
            throw new NullPointerException("protocol is 0");
        AbstractNetMessageHeader header = headCreator.get()
                .setId(this.id)
                .setProtocol(this.protocol)
                .setCode(this.code)
                .setToMessage(this.toMessage)
                .setTime(System.currentTimeMillis())
                .setHead(this.head);
        message.setSessionID(this.sessionID)
                .setHeader(header)
                .setBody(this.body)
                .setTunnel(this.tunnel);
        return message;
    }

}

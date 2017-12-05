package com.tny.game.net.message;

import com.tny.game.net.command.CommandResult;
import com.tny.game.net.message.sign.MessageSignGenerator;
import com.tny.game.net.tunnel.Tunnel;

import java.util.function.Supplier;

/**
 * 抽象MessageBuilder
 * Created by Kun Yang on 2017/3/21.
 */
public abstract class AbstractNetMessageBuilder<UID, M extends AbstractNetMessage<UID>> implements MessageBuilder<UID> {

    private MessageSignGenerator<UID> signGenerator;

    protected int id;

    protected long sessionID;

    private int protocol = -1;

    private int code;

    private Object body;

    private int toMessage;

    protected Tunnel<UID> tunnel;

    private Supplier<M> creator;

    protected AbstractNetMessageBuilder(Supplier<M> creator) {
        this.creator = creator;
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
    public MessageBuilder<UID> setSignGenerator(MessageSignGenerator<UID> generator) {
        this.signGenerator = generator;
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
        M message = this.creator.get();
        if (this.protocol == 0)
            throw new NullPointerException("protocol is 0");
        message.setID(this.id)
                .setSessionID(this.sessionID)
                .setProtocol(this.protocol)
                .setCode(this.code)
                .setBody(this.body)
                .setToMessage(this.toMessage)
                .setTime(System.currentTimeMillis())
                .setTunnel(this.tunnel);
        if (this.signGenerator != null)
            message.setSign(this.signGenerator.generate(this.tunnel, message));
        return message;
    }

}

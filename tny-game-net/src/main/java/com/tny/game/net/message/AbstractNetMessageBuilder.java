package com.tny.game.net.message;

import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.tunnel.Tunnel;

import java.util.function.Supplier;

/**
 * 抽象MessageBuilder
 * Created by Kun Yang on 2017/3/21.
 */
public abstract class AbstractNetMessageBuilder<UID, M extends AbstractNetMessage<UID>> implements MessageBuilder<UID> {

    private MessageSignGenerator<UID> signGenerator;

    protected M message;

    protected Tunnel<UID> tunnel;

    private Supplier<M> creator;

    protected AbstractNetMessageBuilder(Supplier<M> creator) {
        this.creator = creator;
    }

    protected M getMessage() {
        if (message == null)
            this.message = creator.get();
        return this.message;
    }

    @Override
    public MessageBuilder<UID> setContent(MessageContent content) {
        return this.setProtocol(content)
                .setCode(content.getCode())
                .setBody(content.getBody())
                .setToMessage(content.getToMessage());
    }

    @Override
    public MessageBuilder<UID> setID(int id) {
        getMessage().setID(id);
        return this;
    }

    @Override
    public MessageBuilder<UID> setCode(int code) {
        getMessage().setCode(code);
        return this;
    }

    @Override
    public MessageBuilder<UID> setBody(Object body) {
        getMessage().setBody(body);
        return this;
    }

    @Override
    public MessageBuilder<UID> setTime(long time) {
        getMessage().setTime(time);
        return this;
    }

    @Override
    public MessageBuilder<UID> setToMessage(int toMessageID) {
        getMessage().setToMessage(toMessageID);
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
        M message = getMessage();
        if (message.getProtocol() <= 0)
            getMessage().setProtocol(protocol.getProtocol());
        return this;
    }

    @Override
    public MessageBuilder<UID> setCommandResult(CommandResult result) {
        Protocol protocol = result.getProtocol();
        M message = getMessage();
        if (protocol != null)
            message.setProtocol(protocol.getProtocol());
        message.setCode(result.getResultCode().getCode())
                .setBody(result.getBody());
        return this;
    }

    @Override
    public Message<UID> build() {
        M message = getMessage();
        if (message.getProtocol() == 0)
            throw new NullPointerException("protocol is 0");
        message.setTime(System.currentTimeMillis())
                .setTunnel(this.tunnel);
        if (this.signGenerator != null)
            message.setSign(this.signGenerator.generate(this.tunnel, message));
        this.message = null;
        return message;
    }

}
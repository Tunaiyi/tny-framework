package com.tny.game.net.transport.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.transport.MessageContext;

/**
 * Created by Kun Yang on 2017/2/23.
 */
public interface MessageHeaderBuilder {

    MessageHeaderBuilder setID(int id);

    MessageHeaderBuilder setCode(int code);

    default MessageHeaderBuilder setProtocol(Protocol protocol) {
        return this.setProtocol(protocol.getProtocol());
    }

    MessageHeaderBuilder setProtocol(int protocol);

    default MessageHeaderBuilder setCode(ResultCode code) {
        this.setCode(code.getCode());
        return this;
    }

    MessageHeaderBuilder setBody(Object body);

    MessageHeaderBuilder setToMessage(int toMessageID);

    MessageHeaderBuilder setTime(long time);

    MessageHeaderBuilder setCarrier(MessageContext carrier);

    MessageHeaderBuilder setHead(Object head);

    MessageHeader build();

}

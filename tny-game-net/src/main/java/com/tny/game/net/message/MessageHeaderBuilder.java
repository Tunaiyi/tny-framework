package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.session.MessageContent;

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

    MessageHeaderBuilder setCarrier(MessageContent carrier);

    MessageHeaderBuilder setHead(Object head);

    MessageHeader build();

}

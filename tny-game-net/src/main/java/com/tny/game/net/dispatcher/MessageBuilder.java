package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.checker.MessageSignGenerator;

/**
 * Created by Kun Yang on 2017/2/23.
 */
public interface MessageBuilder {

    MessageBuilder setID(int id);

    MessageBuilder setCode(int code);

    default MessageBuilder setCode(ResultCode code) {
        this.setCode(code.getCode());
        return this;
    }

    MessageBuilder setBody(Object body);

    MessageBuilder setTime(long time);

    MessageBuilder setToMessage(Integer toMessageID);

    MessageBuilder setCheckGenerator(MessageSignGenerator generator);

    NetMessage build();

}

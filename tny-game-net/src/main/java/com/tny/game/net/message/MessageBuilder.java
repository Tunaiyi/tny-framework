package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.common.NetMessage;

/**
 * Created by Kun Yang on 2017/2/23.
 */
public interface MessageBuilder<UID> {

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

    NetMessage<UID> build();

}

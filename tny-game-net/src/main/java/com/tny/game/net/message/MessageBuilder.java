package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.session.Session;

/**
 * Created by Kun Yang on 2017/2/23.
 */
public interface MessageBuilder<UID> {

    MessageBuilder<UID> setID(int id);

    MessageBuilder<UID> setCode(int code);

    MessageBuilder<UID> setProtocol(Protocol protocol);

    default MessageBuilder<UID> setCode(ResultCode code) {
        this.setCode(code.getCode());
        return this;
    }

    MessageBuilder<UID> setBody(Object body);

    MessageBuilder<UID> setTime(long time);

    MessageBuilder<UID> setToMessage(int toMessageID);

    MessageBuilder<UID> setSignGenerator(MessageSignGenerator<UID> generator);

    MessageBuilder<UID> setSession(Session<UID> session);

    MessageBuilder<UID> setCommandResult(CommandResult result);

    Message<UID> build();

}

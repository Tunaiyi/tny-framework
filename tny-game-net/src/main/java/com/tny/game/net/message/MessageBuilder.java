package com.tny.game.net.message;

import com.tny.game.common.result.*;
import com.tny.game.net.command.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2017/2/23.
 */
public interface MessageBuilder<UID> {

    MessageBuilder<UID> setId(long id);

    MessageBuilder<UID> setCode(int code);

    MessageBuilder<UID> setProtocol(Protocol protocol);

    default MessageBuilder<UID> setCode(ResultCode code) {
        this.setCode(code.getCode());
        return this;
    }

    MessageBuilder<UID> setBody(Object body);

    MessageBuilder<UID> setCommandResult(CommandResult result);

    MessageBuilder<UID> setCertificate(Certificate<UID> tunnel);

    Message<UID> buildPush();

    Message<UID> buildRequest();

    Message<UID> buildResponse(long toMessage);
}

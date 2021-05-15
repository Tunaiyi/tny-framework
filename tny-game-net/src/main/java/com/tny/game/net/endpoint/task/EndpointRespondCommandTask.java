package com.tny.game.net.endpoint.task;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:33 下午
 */
public class EndpointRespondCommandTask<UID> implements EndpointCommandTask {

    private final Message message;

    private final RespondFuture<UID> respondFuture;

    public EndpointRespondCommandTask(Message message, RespondFuture<UID> respondFuture) {
        this.message = message;
        this.respondFuture = respondFuture;
    }

    @Override
    public Command createCommand() {
        return new RespondFutureCommand<>(this.message, this.respondFuture);
    }

}

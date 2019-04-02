package com.tny.game.net.command.dispatcher;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class RespondFutureCommand<UID> implements Command {

    private RespondFuture<UID> future;

    private Message<UID> message;

    public RespondFutureCommand(Message<UID> message, RespondFuture<UID> future) {
        this.message = message;
        this.future = future;
    }

    @Override
    public void execute() {
        future.complete(message);
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }
}

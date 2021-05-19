package com.tny.game.net.command.dispatcher;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class RespondFutureCommand<UID> implements Command {

    private RespondFuture future;

    private Message message;

    public RespondFutureCommand(Message message, RespondFuture future) {
        this.message = message;
        this.future = future;
    }

    @Override
    public void execute() {
        this.future.complete(this.message);
    }

    @Override
    public boolean isDone() {
        return this.future.isDone();
    }

}

package com.tny.game.net.command.dispatcher;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class RespondFutureCommand implements Command {

    private final MessageRespondAwaiter future;

    private final Message message;

    public RespondFutureCommand(Message message, MessageRespondAwaiter future) {
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

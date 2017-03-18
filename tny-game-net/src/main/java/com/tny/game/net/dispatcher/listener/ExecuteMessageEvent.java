package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.message.Message;
import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.MethodControllerHolder;

public class ExecuteMessageEvent extends DispatchMessageEvent{

    private boolean interrupt;

    private CommandResult result;

    public ExecuteMessageEvent(Message message, MethodControllerHolder controller, CommandResult result) {
        super(message, controller);
        this.result = result;
        this.interrupt = false;
    }

    public ExecuteMessageEvent(Message message, MethodControllerHolder controller) {
        this(message, controller, null);
    }

    /**
     * @return the request
     */
    public Message getMessage() {
        return this.message;
    }

    public CommandResult getResult() {
        return this.result;
    }

    public boolean isHasResult() {
        return this.result != null;
    }

    public void interrupt() {
        interrupt(null);
    }

    public void interrupt(CommandResult result) {
        this.result = result;
        this.interrupt = true;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

}

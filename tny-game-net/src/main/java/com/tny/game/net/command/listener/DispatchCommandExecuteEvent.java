package com.tny.game.net.command.listener;

import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.command.DispatchCommand;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.message.Message;

public class DispatchCommandExecuteEvent<UID> extends DispatchCommandEvent<UID> {

    private boolean interrupt;

    private CommandResult result;

    public DispatchCommandExecuteEvent(DispatchCommand<?> command, Tunnel<UID> tunnel, Message message, MethodControllerHolder controller, CommandResult result) {
        super(command, tunnel, message, controller);
        this.result = result;
        this.interrupt = false;
    }

    public DispatchCommandExecuteEvent(DispatchCommand<?> command, Tunnel<UID> tunnel, Message message, MethodControllerHolder controller) {
        this(command, tunnel, message, controller, null);
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

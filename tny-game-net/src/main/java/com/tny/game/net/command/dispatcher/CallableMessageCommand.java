package com.tny.game.net.command.dispatcher;

import com.tny.game.common.runtime.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.Callable;

import static com.tny.game.net.base.NetLogger.*;

/**
 * <p>
 */
public class CallableMessageCommand<R> extends MessageCommand<MessageCommandContext> {

    private final Callable<R> callable;

    protected CallableMessageCommand(NetTunnel<?> tunnel, Callable<R> callable,
            Message message, MessageDispatcherContext context, EndpointKeeperManager endpointKeeperManager) {
        super(new DefaultMessageCommandContext(CallableMessageCommand.class), tunnel, message, context, endpointKeeperManager);
        this.callable = callable;
    }

    /**
     * 执行 invoke
     */
    @Override
    protected void invoke() throws Exception {
        ProcessTracer tracer = MESSAGE_EXE_RUNNABLE_WATCHER.trace();
        R result = this.callable.call();
        tracer.done();
    }

    @Override
    protected void doInvokeDone(Throwable cause) {
    }

}

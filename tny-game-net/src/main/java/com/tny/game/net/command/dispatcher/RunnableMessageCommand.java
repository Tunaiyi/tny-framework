package com.tny.game.net.command.dispatcher;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * <p>
 */
public class RunnableMessageCommand extends MessageCommand<MessageCommandContext> {

    private final Runnable runnable;

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    protected RunnableMessageCommand(NetTunnel<?> tunnel, Runnable runnable,
            Message message, MessageDispatcherContext context, EndpointKeeperManager endpointKeeperManager) {
        super(new DefaultMessageCommandContext(RunnableMessageCommand.class), tunnel, message, context, endpointKeeperManager);
        this.runnable = runnable;
    }

    /**
     * 执行 invoke
     */
    @Override
    protected void invoke() {
        this.runnable.run();
    }

    @Override
    protected void doInvokeDone(Throwable cause) {
    }

}

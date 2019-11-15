package com.tny.game.net.command.dispatcher;

import com.tny.game.net.command.listener.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/5/27.
 */
public abstract class DispatchContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(DispatchContext.class);

    protected NetTunnel<Object> tunnel;

    protected NetMessage<Object> message;

    protected MessageDispatcherContext context;


    protected DispatchContext(MessageDispatcherContext context, NetTunnel<?> tunnel, Message<?> message) {
        this.tunnel = as(tunnel);
        this.message = as(message);
        this.context = context;
    }


    public String getAppType() {
        return context.getAppContext().getAppType();
    }

    public String getScopeType() {
        return context.getAppContext().getScopeType();
    }

    protected void fireExecuteStart() {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onExecuteStart(this);
            } catch (Throwable e) {
                LOGGER.error("on fireExecuteStart exception", e);
            }
        }
    }

    protected void fireExecuteEnd(Throwable cause) {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onExecuteEnd(this, cause);
            } catch (Throwable e) {
                LOGGER.error("on fireExecuteEnd exception", e);
            }
        }
    }

    protected void fireDone(Throwable cause) {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onDone(this, cause);
            } catch (Throwable e) {
                LOGGER.error("on fireDone( exception", e);
            }
        }
    }

}


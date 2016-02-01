package com.tny.game.net.client.rmi;

import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.dispatcher.*;
import com.tny.game.net.dispatcher.exception.DispatchException;

public abstract class NetRMIService implements RMIService {

    /**
     * 会话验证器
     */
    protected AppContext context;

    private MessageDispatcher messageDispatcher;

    public NetRMIService(AppContext context) {
        this.context = context;
        this.messageDispatcher = context.getMessageDispatcher();
    }

    public void startService() {
        this.context.initContext((context) -> {
        });
    }

    @Override
    public Response send(Request request) {
        ServerSession session = null;
        try {
            session = this.createSession(request);
            CommandResult result = this.messageDispatcher.dispatch(request, session, this.context).execute();
            if (result != null) {
                return session.getMessageBuilderFactory()
                        .newResponseBuilder()
                        .setProtocol(request)
                        .setCommandResult(result)
                        .build();
            }
        } catch (DispatchException e) {
            Response response = session.getMessageBuilderFactory()
                    .newResponseBuilder()
                    .setProtocol(request)
                    .setResult(e.getResultCode())
                    .build();
            CoreLogger.log(session, response);
            return response;
        }
        return null;
    }

    protected abstract ServerSession createSession(Request request) throws DispatchException;

}

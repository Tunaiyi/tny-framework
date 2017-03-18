package com.tny.game.net.client.rmi;

import com.tny.game.log.NetLogger;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.message.MessageDispatcher;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Response;
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
        RMISession session = null;
        try {
            session = this.createSession(request);
            CommandResult result = this.messageDispatcher.dispatch(request, session, this.context).invoke();
            if (result != null) {
                return session.getMessageBuilderFactory()
                        .newResponseBuilder(session)
                        .setProtocol(request)
                        .setCommandResult(result)
                        .build();
            }
        } catch (DispatchException e) {
            Response response = session.getMessageBuilderFactory()
                    .newResponseBuilder(session)
                    .setProtocol(request)
                    .setResult(e.getResultCode())
                    .build();
            NetLogger.log(session, response);
            return response;
        }
        return null;
    }

    protected abstract RMISession createSession(Request request) throws DispatchException;

}

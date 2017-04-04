package com.tny.game.net.client.rmi;

public abstract class NetRMIService {
    // implements RMIService {
    //
    // /**
    //  * 会话验证器
    //  */
    // protected AppContext context;
    //
    // private MessageDispatcher messageDispatcher;
    //
    // private DefaultMessageBuilderFactory<Object> messageBuilderFactory;
    //
    // public NetRMIService(AppContext context) {
    //     this.context = context;
    //     this.messageDispatcher = context.getMessageDispatcher();
    // }
    //
    // public void startService() {
    //     this.context.initContext((context) -> {
    //     });
    // }
    //
    // // @Override
    // // public <FID, TID> Message<TID> send(Message<FID> message) {
    // //     RMISession<FID> session = null;
    // //     try {
    // //         session = this.loadOrCreateSession(message);
    // //         CommandResult result = this.messageDispatcher.dispatch(message, session).invoke();
    // //         if (result != null) {
    // //             return messageBuilderFactory
    // //                     .newMessageBuilder()
    // //                     .setSession(session)
    // //                     .setCommandResult(result)
    // //                     .setProtocol(message)
    // //                     .build();
    // //         }
    // //     } catch (DispatchException e) {
    // //         Response response = session.getMessageBuilderFactory()
    // //                 .newResponseBuilder(session)
    // //                 .setProtocol(request)
    // //                 .setResult(e.getResultCode())
    // //                 .build();
    // //         NetLogger.log(session, response);
    // //         return response;
    // //     }
    // //     return null;
    // // }
    //
    // protected abstract RMISession loadOrCreateSession(Message<?> request) throws DispatchException;
    //
}

package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.actor.Answer;
import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.StageUtils;
import com.tny.game.actor.stage.TaskStage;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodeType;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Message;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.checker.MessageChecker;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.listener.DispatchExceptionEvent;
import com.tny.game.net.dispatcher.listener.DispatcherMessageErrorEvent;
import com.tny.game.net.dispatcher.listener.DispatcherMessageEvent;
import com.tny.game.net.dispatcher.listener.DispatcherMessageListener;
import com.tny.game.worker.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 抽象消息派发器
 *
 * @author KGTny
 *         <p>
 *         抽象消息派发器
 *         <p>
 *         <p>
 *         实现对controllerMap的初始化,派发消息流程<br>
 */
public abstract class NetMessageDispatcher implements MessageDispatcher {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(CoreLogger.DISPATCHER);

    /**
     * Controller Map
     */
    final Map<Integer, MethodControllerHolder> methodHolder = new CopyOnWriteMap<>();

    private boolean checkTimeOut = true;

    /**
     * 派发错误监听器
     */
    protected final List<DispatcherMessageListener> listeners = new CopyOnWriteArrayList<>();

    protected AppContext context;

    NetMessageDispatcher(boolean checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
    }

    @Override
    public DispatcherCommand<CommandResult> dispatch(Message message, NetSession session) throws DispatchException {

        // 获取方法持有器
        final MethodControllerHolder methodHolder = this.methodHolder.get(message.getProtocol());
        if (methodHolder == null) {
            NetMessageDispatcher.DISPATCHER_LOG.error("没有存在 {} 方法 ", message.getProtocol());
            throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
        }

        if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
            NetMessageDispatcher.DISPATCHER_LOG.debug("消息:{}\n 消息调用{}.{} 业务方法", LogUtils.msg(message.toString(), methodHolder.getMethodClass(), methodHolder.getName()));

        String serverType = context.getScopeType();
        if (serverType != null && !methodHolder.isCanCall(serverType)) {
            NetMessageDispatcher.DISPATCHER_LOG.error("{} 服务器无法调用 {}.{} 业务方法", LogUtils.msg(serverType, methodHolder.getMethodClass(), methodHolder.getName()));
            throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
        }
        return new MessageDispatcherCommand(context, message, session, methodHolder);

    }

    @Override
    public void addDispatcherRequestListener(final DispatcherMessageListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeDispatcherRequestListener(final DispatcherMessageListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void clearDispatcherRequestListener() {
        this.listeners.clear();
    }

    /**
     * 触发未登陆事件
     * <p>
     * <p>
     * 触发未登陆事件<br>
     *
     * @param event 未登陆事件
     */
    private void fireExecuteDispatchException(DispatchExceptionEvent event) {
        for (DispatcherMessageListener listener : this.listeners) {
            try {
                listener.executeDispatchException(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.unlogin 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行错误事件
     * <p>
     * <p>
     * 触发业务运行错误事件<br>
     *
     * @param event 业务运行错误事件
     */
    private void fireExecuteException(DispatcherMessageErrorEvent event) {
        for (DispatcherMessageListener listener : this.listeners) {
            try {
                listener.executeException(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.executeException 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行事件
     * <p>
     * <p>
     * 触发业务运行事件<br>
     *
     * @param event 业务运行事件
     */
    private void fireExecute(DispatcherMessageEvent event) {
        for (DispatcherMessageListener listener : this.listeners) {
            try {
                listener.execute(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.execute 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行完成事件
     * <p>
     * <p>
     * 触发业务运行完成事件<br>
     *
     * @param event 业务运行完成事件
     */
    private void fireFinish(DispatcherMessageEvent event) {
        for (DispatcherMessageListener listener : this.listeners) {
            try {
                listener.finish(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.finish 监听器异常", listener.getClass(), e);
            }
        }
    }

    public abstract void initDispatcher(AppContext appContext);

    private class MessageDispatcherCommand implements DispatcherCommand<CommandResult> {

        protected AppContext appContext;
        protected Message message;
        protected NetSession<?> session;

        private final MethodControllerHolder controller;

        private CommandFuture commandFuture;

        private boolean executed;

        Callback<Object> callback;
        boolean done;

        private MessageDispatcherCommand(AppContext appContext, Message message, NetSession<?> session, MethodControllerHolder controller) {
            this.appContext = appContext;
            this.message = message;
            this.session = session;
            this.controller = controller;
        }

        @SuppressWarnings("unchecked")
        public void setCallback(Callback<?> callback) {
            this.callback = (Callback<Object>) callback;
        }

        @Override
        public String getName() {
            return this.getClass() + "#" + this.message.getProtocol();
        }

        @Override
        public boolean isDone() {
            return done;
        }

        private CommandFuture getCommandFuture(Object value) {
            if (value instanceof TaskStage)
                return new TaskStageCommandFuture((TaskStage) value);
            else if (value instanceof Answer)
                return new AnswerCommandFuture((Answer<?>) value);
            return null;
        }

        private void handleResult(ResultCode code, Object value, Throwable cause) {
            if (callback != null) {
                try {
                    callback.callback(code, value, cause);
                } catch (Throwable e) {
                    DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 执行回调方法 {} 异常", controller.getMethodClass(), controller.getName(), callback.getClass(), e);
                }
            }
            if (code.getType() != ResultCodeType.ERROR) {
                this.session.sendMessage(this.message, code, value);
            } else {
                this.session.sendMessage(this.message, MessageContent.of(code, value)
                        .setSentHandler(m ->
                                this.appContext.getSessionHolder().offline(this.session)
                        ));
            }
        }

        @Override
        public CommandResult invoke() {
            if (this.executed)
                return null;
            CommandResult result;
            try {
                result = this.doExecute();
            } catch (Throwable e) {
                result = this.handleDispatchException(e);
                DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 执行方法异常 {} - {} ", controller.getMethodClass(), controller.getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
            } finally {
                this.executed = true;
            }
            return result;
        }

        @Override
        public void execute() {
            CurrentCMD.setCurrent(this.message.getUserID(), this.message.getProtocol());
            if (done)
                return;
            if (!executed) {
                CommandResult result = invoke();
                Object value = result.getBody();
                this.commandFuture = getCommandFuture(value);
                if (this.commandFuture == null) {
                    try {
                        this.handleResult(result.getResultCode(), result.getBody(), null);
                    } finally {
                        this.done = true;
                    }
                }
            }
            if (!done && this.commandFuture != null && this.commandFuture.isDone()) {
                try {
                    try {
                        if (this.commandFuture.isSuccess()) {
                            Object value = this.commandFuture.getResult();
                            this.handleResult(ResultCode.SUCCESS, value, null);
                        } else {
                            CommandResult result = handleDispatchException(this.commandFuture.getCause());
                            DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 轮询Command结束异常 {} - {} ", controller.getMethodClass(), controller.getName(), result.getResultCode(), result.getResultCode().getMessage(), this.commandFuture.getCause());
                            this.handleResult(result.getResultCode(), result.getBody(), this.commandFuture.getCause());
                        }
                    } finally {
                        this.done = true;
                    }
                } catch (Throwable e) {
                    try {
                        CommandResult result = handleDispatchException(e);
                        DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 轮询Command结束异常 {} - {} ", controller.getMethodClass(), controller.getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
                        this.handleResult(result.getResultCode(), result.getBody(), e);
                    } finally {
                        this.done = true;
                    }
                }
            }
        }


        private CommandResult handleDispatchException(Throwable e) {
            CommandResult result;
            if (e instanceof DispatchException) {
                DispatchException dex = (DispatchException) e;
                NetMessageDispatcher.DISPATCHER_LOG.error(dex.getMessage(), dex);
                NetMessageDispatcher.this.fireExecuteDispatchException(new DispatchExceptionEvent(this.message, this.controller, dex));
                result = ResultFactory.create(dex.getResultCode(), null);
            } else if (e instanceof InvocationTargetException) {
                NetMessageDispatcher.DISPATCHER_LOG.error("", e);
                Throwable ex = ((InvocationTargetException) e).getTargetException();
                result = this.handleDispatchException(ex);
            } else {
                NetMessageDispatcher.DISPATCHER_LOG.error("Executing " + this.controller.getMethodClass() + "." + this.controller.getName() + " exception", e);
                DispatcherMessageErrorEvent event = new DispatcherMessageErrorEvent(this.message, this.controller, e);
                NetMessageDispatcher.this.fireExecuteException(event);
                result = event.getResult();
                if (result != null) {
                    return result;
                } else {
                    return ResultFactory.create(CoreResponseCode.EXECUTE_EXCEPTION, null);
                }
            }
            return result;
        }

        private CommandResult doExecute() throws Exception {

            this.checkExecutable();

            // 调用方法
            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行事件", this.controller.getMethodClass(), this.controller.getName());
            NetMessageDispatcher.this.fireExecute(new DispatcherMessageEvent(this.message, this.controller));

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}执行业务", this.controller.getMethodClass(), this.controller.getName());
            CommandResult result = this.controller.execute(this.message);

            // 执行结束触发命令执行完成事件
            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行完成事件", this.controller.getMethodClass(), this.controller.getName());
            DispatcherMessageEvent finish = new DispatcherMessageEvent(this.message, this.controller, result);
            NetMessageDispatcher.this.fireFinish(finish);

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行完成", this.controller.getMethodClass(), this.controller.getName());

            result = finish.getResult();
            if (result == null)
                result = ResultFactory.SUCC;
            return result;

        }

        private void checkExecutable() throws DispatchException {

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测消息 {}.{} 业务方法超时", this.controller.getMethodClass(), this.controller.getName());
            // 是否需要做超时判断
            if (NetMessageDispatcher.this.checkTimeOut && this.controller.isTimeOut() && System.currentTimeMillis() + this.controller.getRequestLife() > this.message.getTime()) {
                NetMessageDispatcher.DISPATCHER_LOG.error("消息 {}.{} 业务方法超时", this.controller.getMethodClass(), this.controller.getName());
                throw new DispatchException(CoreResponseCode.REQUEST_TIMEOUT);
            }

            // 是否需要做登录校验,判断是否已经登录
            NetSessionHolder sessionHolder = this.appContext.getSessionHolder();

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测消息 {}.{} 所需登陆权限", this.controller.getMethodClass(), this.controller.getName());
            if (this.controller.isAuth() && !this.session.isLogin()) {
                List<AuthProvider> authProviders = this.appContext.getAuthProviders();
                for (AuthProvider provider : authProviders) {
                    if (!provider.isCanValidate(this.message))
                        continue;
                    LoginCertificate loginInfo = provider.validate(this.message);
                    if (loginInfo == null || !loginInfo.isLogin() || !sessionHolder.online(this.session, loginInfo)) {
                        NetMessageDispatcher.DISPATCHER_LOG.error("用户没有登陆无法消息 {}.{} 业务方法", this.controller.getMethodClass(), this.controller.getName());
                        throw new DispatchException(CoreResponseCode.UNLOGIN);
                    } else {
                        break;
                    }
                }
            }

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测用户调用 {}.{} 权限", this.controller.getMethodClass(), this.controller.getName());
            if (!this.controller.isUserGroup(this.session.getGroup())) {
                NetMessageDispatcher.DISPATCHER_LOG.error("{}用户组用户无法调用{}.{} 业务方法", LogUtils.msg(this.session.getGroup(), this.controller.getMethodClass(), this.controller.getName()));
                throw new DispatchException(CoreResponseCode.NO_PERMISSIONS);
            }

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测调用 {}.{} 消息是否被篡改", this.controller.getMethodClass(), this.controller.getName());

            List<MessageChecker> checkers = this.session.getCheckers();
            if (this.controller.isCheck() && !checkers.isEmpty()) {
                // 检测消息的正确性
                for (MessageChecker checker : checkers) {
                    // 获取普通调用的校验码密钥
                    if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                        NetMessageDispatcher.DISPATCHER_LOG.debug("调用 {}.{} 检测Request - {}", this.controller.getMethodClass(), this.controller.getName(), checker.getClass());
                    ResultCode resultCode;
                    if (!checker.isCheck(this.message))
                        continue;
                    if ((resultCode = checker.match(this.message)).isFailure()) {
                        NetMessageDispatcher.DISPATCHER_LOG.error("调用 {}.{} 检测Request - {} 失败原因: {} - {}", this.controller.getMethodClass(), this.controller.getName(), checker.getClass(), resultCode, resultCode.getMessage());
                        throw new DispatchException(CoreResponseCode.FALSIFY);
                    }
                }
            }

        }

    }


    // private class ResponseDispatcherCommand extends AbstractDispatcherCommand<ClientSession, Response, Void> {
    //
    //     private MessageFuture<?> future;
    //
    //     private ResponseDispatcherCommand(AppContext appContext, Response response, ClientSession session, MessageFuture<?> future) {
    //         super(appContext, response, session);
    //         this.future = future;
    //     }
    //
    //     @Override
    //     @SuppressWarnings("unchecked")
    //     public Void invoke() {
    //         Object body = this.message.getBody(Object.class);
    //         if (body == null)
    //             return null;
    //         ResponseHandlerHolder handlerHolder = appContext.getResponseHandlerHolder();
    //         if (handlerHolder == null)
    //             return null;
    //         for (ResponseHandler<?> mon : handlerHolder.getMonitorHolderList(body)) {
    //             ResponseHandler<Object> monitor = (ResponseHandler<Object>) mon;
    //             if (monitor.getMonitorType().isHandle(this.message.getID())) {
    //                 try {
    //                     List<Protocol> includes = monitor.includeProtocols();
    //                     if (!this.match(this.message, includes, true))
    //                         continue;
    //                     List<Protocol> excludes = monitor.excludeProtocols();
    //                     if (this.match(this.message, excludes, false))
    //                         continue;
    //                     monitor.handle(this.session, this.message, this.message.getBody(Object.class));
    //                 } catch (Throwable e) {
    //                     NetMessageDispatcher.DISPATCHER_LOG.error("call monitor handle {}", monitor.getClass(), e);
    //                 }
    //             }
    //         }
    //         if (this.future != null)
    //             this.future.setResponse(this.message);
    //         return null;
    //     }
    //
    //     private boolean match(Message message, List<Protocol> filters, boolean defResult) {
    //         if (filters != null && !filters.isEmpty()) {
    //             for (Protocol controller : filters) {
    //                 if (controller.isOwn(message)) {
    //                     return true;
    //                 }
    //             }
    //             return false;
    //         }
    //         return defResult;
    //     }
    //
    //     @Override
    //     public void execute() {
    //         invoke();
    //         this.done = true;
    //     }
    //
    // }

    private interface CommandFuture {

        boolean isDone();

        boolean isSuccess();

        Throwable getCause();

        Object getResult();

    }

    private static class TaskStageCommandFuture implements CommandFuture {

        private TaskStage stage;

        TaskStageCommandFuture(TaskStage stage) {
            this.stage = stage;
        }

        @Override
        public boolean isDone() {
            return stage.isDone();
        }

        @Override
        public boolean isSuccess() {
            return stage.isSuccess();
        }

        @Override
        public Throwable getCause() {
            return stage.getCause();
        }

        @Override
        public Object getResult() {
            return StageUtils.getCause(stage);
        }

    }

    private static class AnswerCommandFuture implements CommandFuture {

        private Answer<?> answer;

        AnswerCommandFuture(Answer<?> answer) {
            this.answer = answer;
        }

        @Override
        public boolean isDone() {
            return answer.isDone();
        }

        @Override
        public boolean isSuccess() {
            return !answer.isFail();
        }

        @Override
        public Throwable getCause() {
            return answer.getCause();
        }

        @Override
        public Object getResult() {
            if (this.answer instanceof VoidAnswer) {
                return null;
            } else if (this.answer instanceof TypeAnswer) {
                return ((TypeAnswer) this.answer).achieve().get();
            }
            return null;
        }

    }

}

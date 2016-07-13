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
import com.tny.game.net.base.Protocol;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.listener.DispatchExceptionEvent;
import com.tny.game.net.dispatcher.listener.DispatcherRequestErrorEvent;
import com.tny.game.net.dispatcher.listener.DispatcherRequestEvent;
import com.tny.game.net.dispatcher.listener.DispatcherRequestListener;
import com.tny.game.worker.Callback;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 抽象请求派发器
 *
 * @author KGTny
 *         <p>
 *         抽象请求派发器
 *         <p>
 *         <p>
 *         实现对controllerMap的初始化,派发请求流程<br>
 */
public abstract class NetMessageDispatcher implements MessageDispatcher {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(CoreLogger.DISPATCHER);

    /**
     * Controller Map
     */
    final Map<Integer, MethodHolder> methodHolder = new CopyOnWriteMap<>();

    private boolean checkTimeOut = true;

    /**
     * 派发错误监听器
     */
    protected final List<DispatcherRequestListener> listeners = new CopyOnWriteArrayList<>();

    NetMessageDispatcher(boolean checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
    }

    @Override
    public DispatcherCommand<CommandResult> dispatch(Request request, ServerSession session, AppContext context) throws DispatchException {
        request.requestBy(session);

        // 获取方法持有器
        final MethodHolder methodHolder = this.methodHolder.get(request.getProtocol());
        if (methodHolder == null) {
            NetMessageDispatcher.DISPATCHER_LOG.error("没有存在 {} 方法 ", request.getProtocol());
            throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
        }

        if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
            NetMessageDispatcher.DISPATCHER_LOG.debug("请求:{}\n 请求调用{}.{} 业务方法", LogUtils.msg(request.toString(), methodHolder.getMethodClass(), methodHolder.getName()));

        String serverType = context.getScopeType();
        if (serverType != null && !methodHolder.isCanCall(serverType)) {
            NetMessageDispatcher.DISPATCHER_LOG.error("{} 服务器无法调用 {}.{} 业务方法", LogUtils.msg(serverType, methodHolder.getMethodClass(), methodHolder.getName()));
            throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
        }
        return new RequestDispatcherCommand(context, methodHolder, request, session);

    }

    @Override
    public DispatcherCommand<Void> dispatch(Response response, ClientSession session, AppContext context) throws DispatchException {
        final MessageFuture<?> future = response.getID() >= 0 ? session.takeFuture(response.getID()) : null;
        if (future != null) {
            future.setResponse(response);
        }
        return new ResponseDispatcherCommand(context, response, session, future);
    }

    @Override
    public void addDispatcherRequestListener(final DispatcherRequestListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeDispatcherRequestListener(final DispatcherRequestListener listener) {
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
        for (DispatcherRequestListener listener : this.listeners) {
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
    private void fireExecuteException(DispatcherRequestErrorEvent event) {
        for (DispatcherRequestListener listener : this.listeners) {
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
    private void fireExecute(DispatcherRequestEvent event) {
        for (DispatcherRequestListener listener : this.listeners) {
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
    private void fireFinish(DispatcherRequestEvent event) {
        for (DispatcherRequestListener listener : this.listeners) {
            try {
                listener.finish(event);
            } catch (Exception e) {
                NetMessageDispatcher.DISPATCHER_LOG.error("处理 {}.finish 监听器异常", listener.getClass(), e);
            }
        }
    }

    public abstract void initDispatcher(AppContext appContext);

    private static abstract class AbstractDispatcherCommand<S extends NetSession, H extends Message, O> implements DispatcherCommand<O> {

        final AppContext appContext;
        protected H message;
        protected S session;

        Callback<Object> callback;
        boolean done;

        private AbstractDispatcherCommand(AppContext appContext, H message, S session) {
            this.appContext = appContext;
            this.message = message;
            this.session = session;
        }

        @SuppressWarnings("unchecked")
        public void setCallback(Callback<?> callback) {
            this.callback = (Callback<Object>) callback;
        }

        @Override
        public long getUserID() {
            return this.session.getUID();
        }

        @Override
        public String getName() {
            return this.getClass() + "#" + this.message.getProtocol();
        }

        @Override
        public boolean isDone() {
            return done;
        }

    }

    private class ResponseDispatcherCommand extends AbstractDispatcherCommand<ClientSession, Response, Void> {

        private MessageFuture<?> future;

        private ResponseDispatcherCommand(AppContext appContext, Response response, ClientSession session, MessageFuture<?> future) {
            super(appContext, response, session);
            this.future = future;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Void invoke() {
            if (this.future != null)
                this.future.setResponse(this.message);
            Object body = this.message.getBody(Object.class);
            if (body == null)
                return null;
            List<ResponseMonitor<?>> monitors = new ArrayList<>();//this.session.getResponseMonitors(body);
            for (ResponseMonitor<?> mon : monitors) {
                ResponseMonitor<Object> monitor = (ResponseMonitor<Object>) mon;
                if (monitor.getMonitorType().isHandle(this.message.getID())) {
                    try {
                        List<Protocol> includes = monitor.includeController();
                        if (!this.match(this.message, includes, true))
                            continue;
                        List<Protocol> excludes = monitor.excludeController();
                        if (this.match(this.message, excludes, false))
                            continue;
                        monitor.handle(this.session, this.message, this.message.getBody(Object.class));
                    } catch (Throwable e) {
                        NetMessageDispatcher.DISPATCHER_LOG.error("call monitor handle {}", monitor.getClass(), e);
                    }
                }
            }
            return null;
        }

        private boolean match(Message message, List<Protocol> filters, boolean defResult) {
            if (filters != null && !filters.isEmpty()) {
                for (Protocol controller : filters) {
                    if (controller.isOwn(message)) {
                        return true;
                    }
                }
                return false;
            }
            return defResult;
        }

        @Override
        public void execute() {
            invoke();
            this.done = true;
        }

    }

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

    private class RequestDispatcherCommand extends AbstractDispatcherCommand<ServerSession, Request, CommandResult> {

        private final MethodHolder methodHolder;

        private CommandFuture commandFuture;

        private boolean executed;

        private RequestDispatcherCommand(AppContext appContext, MethodHolder methodHolder, Request request, ServerSession session) {
            super(appContext, request, session);
            this.methodHolder = methodHolder;
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
                    DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 执行回调方法 {} 异常", methodHolder.getMethodClass(), methodHolder.getName(), callback.getClass(), e);
                }
            }
            Optional<ChannelFuture> future = this.session.response(this.message, code, value);
            if (future != null && code.getType() == ResultCodeType.ERROR) {
                future.ifPresent(result ->
                        result.addListener(f -> RequestDispatcherCommand.this.appContext.getSessionHolder().offline(RequestDispatcherCommand.this.session))
                );
            }
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
                            DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 轮询Command结束异常 {} - {} ", methodHolder.getMethodClass(), methodHolder.getName(), result.getResultCode(), result.getResultCode().getMessage(), this.commandFuture.getCause());
                            this.handleResult(result.getResultCode(), result.getBody(), this.commandFuture.getCause());
                        }
                    } finally {
                        this.done = true;
                    }
                } catch (Throwable e) {
                    try {
                        CommandResult result = handleDispatchException(e);
                        DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 轮询Command结束异常 {} - {} ", methodHolder.getMethodClass(), methodHolder.getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
                        this.handleResult(result.getResultCode(), result.getBody(), e);
                    } finally {
                        this.done = true;
                    }
                }
            }
        }

        @Override
        public CommandResult invoke() {
            if (this.executed)
                return null;
            CommandResult result = null;
            try {
                result = this.doExecute();
            } catch (Throwable e) {
                result = this.handleDispatchException(e);
                DISPATCHER_LOG.error("#Dispatcher#DispatcherCommand [{}.{}] 执行方法异常 {} - {} ", methodHolder.getMethodClass(), methodHolder.getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
            } finally {
                this.executed = true;
            }
            return result;
        }

        private CommandResult handleDispatchException(Throwable e) {
            CommandResult result;
            if (e instanceof DispatchException) {
                DispatchException dex = (DispatchException) e;
                NetMessageDispatcher.DISPATCHER_LOG.error(dex.getMessage(), dex);
                NetMessageDispatcher.this.fireExecuteDispatchException(new DispatchExceptionEvent(this.message, this.methodHolder, dex));
                result = ResultFactory.create(dex.getResultCode(), null);
            } else if (e instanceof InvocationTargetException) {
                NetMessageDispatcher.DISPATCHER_LOG.error("", e);
                Throwable ex = ((InvocationTargetException) e).getTargetException();
                result = this.handleDispatchException(ex);
            } else {
                NetMessageDispatcher.DISPATCHER_LOG.error("Executing " + this.methodHolder.getMethodClass() + "." + this.methodHolder.getName() + " exception", e);
                DispatcherRequestErrorEvent event = new DispatcherRequestErrorEvent(this.message, this.methodHolder, e);
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
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行事件", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            NetMessageDispatcher.this.fireExecute(new DispatcherRequestEvent(this.message, this.methodHolder));

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}执行业务", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            CommandResult result = this.methodHolder.execute(this.message);

            // 执行结束触发命令执行完成事件
            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行完成事件", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            DispatcherRequestEvent finish = new DispatcherRequestEvent(this.message, this.methodHolder, result);
            NetMessageDispatcher.this.fireFinish(finish);

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("{}.{}触发业务执行完成", this.methodHolder.getMethodClass(), this.methodHolder.getName());

            result = finish.getResult();
            if (result == null)
                result = ResultFactory.SUCC;
            return result;

        }

        private void checkExecutable() throws DispatchException {

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测请求 {}.{} 业务方法超时", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            // 是否需要做登录校验,判断是否已经登录
            if (NetMessageDispatcher.this.checkTimeOut && this.methodHolder.isTimeOut() && this.message.isTimeOut(this.methodHolder.getRequestLife())) {
                NetMessageDispatcher.DISPATCHER_LOG.error("请求 {}.{} 业务方法超时", this.methodHolder.getMethodClass(), this.methodHolder.getName());
                throw new DispatchException(CoreResponseCode.REQUEST_TIMEOUT);
            }

            // 是否需要做登录校验,判断是否已经登录
            NetSessionHolder sessionHolder = this.appContext.getSessionHolder();

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测请求 {}.{} 所需登陆权限", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            if (this.methodHolder.isAuth() && !this.message.isLogin()) {
                List<AuthProvider> authProviders = this.appContext.getAuthProviders();
                for (AuthProvider provider : authProviders) {
                    if (!provider.isCanValidate(this.message))
                        continue;
                    LoginCertificate loginInfo = provider.validate(this.message);
                    if (loginInfo == null || !loginInfo.isLogin() || !sessionHolder.online(this.session, loginInfo)) {
                        NetMessageDispatcher.DISPATCHER_LOG.error("用户没有登陆无法请求 {}.{} 业务方法", this.methodHolder.getMethodClass(), this.methodHolder.getName());
                        throw new DispatchException(CoreResponseCode.UNLOGIN);
                    } else {
                        break;
                    }
                }
            }

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测用户调用 {}.{} 权限", this.methodHolder.getMethodClass(), this.methodHolder.getName());
            if (!this.methodHolder.isUserGroup(this.session.getGroup())) {
                NetMessageDispatcher.DISPATCHER_LOG.error("{}用户组用户无法调用{}.{} 业务方法", LogUtils.msg(this.session.getGroup(), this.methodHolder.getMethodClass(), this.methodHolder.getName()));
                throw new DispatchException(CoreResponseCode.NO_PERMISSIONS);
            }

            if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                NetMessageDispatcher.DISPATCHER_LOG.debug("检测调用 {}.{} 请求是否被篡改", this.methodHolder.getMethodClass(), this.methodHolder.getName());

            List<RequestChecker> checkers = this.session.getCheckers();
            if (this.methodHolder.isCheck() && !checkers.isEmpty()) {
                // 检测请求的正确性
                for (RequestChecker checker : checkers) {
                    // 获取普通调用的校验码密钥
                    if (NetMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                        NetMessageDispatcher.DISPATCHER_LOG.debug("调用 {}.{} 检测Request - {}", this.methodHolder.getMethodClass(), this.methodHolder.getName(), checker.getClass());
                    ResultCode resultCode;
                    if ((resultCode = checker.match(this.message)).isFailure()) {
                        NetMessageDispatcher.DISPATCHER_LOG.error("调用 {}.{} 检测Request - {} 失败原因: {} - {}", this.methodHolder.getMethodClass(), this.methodHolder.getName(), checker.getClass(), resultCode, resultCode.getMessage());
                        throw new DispatchException(CoreResponseCode.FALSIFY);
                    }
                }
            }

        }

    }

}

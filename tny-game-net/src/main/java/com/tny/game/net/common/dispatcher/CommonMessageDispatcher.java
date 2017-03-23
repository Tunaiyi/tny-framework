package com.tny.game.net.common.dispatcher;

import com.tny.game.actor.Answer;
import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.StageUtils;
import com.tny.game.actor.stage.TaskStage;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodeType;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.command.MessageCommand;
import com.tny.game.net.command.MessageDispatcher;
import com.tny.game.net.command.listener.DispatchExceptionEvent;
import com.tny.game.net.command.listener.DispatchMessageErrorEvent;
import com.tny.game.net.command.listener.DispatchMessageEvent;
import com.tny.game.net.command.listener.ExecuteMessageEvent;
import com.tny.game.net.command.listener.MessageDispatcherListener;
import com.tny.game.net.common.CheckerHolder;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.holder.NetSessionHolder;
import com.tny.game.worker.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public abstract class CommonMessageDispatcher implements MessageDispatcher {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    /**
     * Controller Map
     */
    protected Map<Integer, Map<MessageMode, MethodControllerHolder>> methodHolder;

    /**
     * 派发错误监听器
     */
    protected final List<MessageDispatcherListener> listeners = new CopyOnWriteArrayList<>();

    protected AppContext context;

    @Override
    public MessageCommand<CommandResult> dispatch(Message message, NetSession session) throws DispatchException {

        // 获取方法持有器
        MethodControllerHolder controller = null;
        final Map<MessageMode, MethodControllerHolder> controllerMap = this.methodHolder.get(message.getProtocol());
        if (controllerMap != null)
            controller = controllerMap.get(message.getMode());
        // @TODO 移到MessageChecker
        // String serverType = context.getScopeType();
        // if (serverType != null && !methodHolder.isCanCall(serverType)) {
        //     NetMessageDispatcher.DISPATCHER_LOG.error("{} 服务器无法调用此协议", LogUtils.msg(serverType, methodHolder.getMethodClass(), methodHolder.getName()));
        //     throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
        // }
        return new MessageMessageCommand(context, message, session, controller);

    }

    @Override
    public void addDispatcherRequestListener(final MessageDispatcherListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeDispatcherRequestListener(final MessageDispatcherListener listener) {
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
        for (MessageDispatcherListener listener : this.listeners) {
            try {
                listener.executeDispatchException(event);
            } catch (Exception e) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("处理 {}.executeDispatchException 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发接收消息消息事件
     * <p>
     * <p>
     * 触发接收消息消息事件<br>
     *
     * @param event 接收消息
     */
    private void fireReceive(ExecuteMessageEvent event) {
        for (MessageDispatcherListener listener : this.listeners) {
            try {
                listener.receive(event);
            } catch (Exception e) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("处理 {}.receive", listener.getClass(), e);
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
    private void fireExecuteException(DispatchMessageErrorEvent event) {
        for (MessageDispatcherListener listener : this.listeners) {
            try {
                listener.executeException(event);
            } catch (Exception e) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("处理 {}.executeException 监听器异常", listener.getClass(), e);
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
    private void fireExecute(ExecuteMessageEvent event) {
        for (MessageDispatcherListener listener : this.listeners) {
            try {
                listener.execute(event);
            } catch (Exception e) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("处理 {}.execute 监听器异常", listener.getClass(), e);
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
    private void fireExecuteFinish(ExecuteMessageEvent event) {
        for (MessageDispatcherListener listener : this.listeners) {
            try {
                listener.executeFinish(event);
            } catch (Exception e) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("处理 {}.executeFinish 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发消息处理完成事件
     * <p>
     * <p>
     * 触发业务运行完成事件<br>
     *
     * @param event 业务运行完成事件
     */
    private void fireDispatchFinish(DispatchMessageEvent event) {
        for (MessageDispatcherListener listener : this.listeners) {
            try {
                listener.dispatchFinish(event);
            } catch (Exception e) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("处理 {}.dispatchFinish 监听器异常", listener.getClass(), e);
            }
        }
    }

    public abstract void initDispatcher(AppContext appContext);

    private class MessageMessageCommand implements MessageCommand<CommandResult> {

        protected AppContext appContext;
        protected Message message;
        protected NetSession session;

        private MethodControllerHolder controller;

        private CommandFuture commandFuture;

        private boolean executed;

        Callback<Object> callback;

        boolean done;

        private MessageMessageCommand(AppContext appContext, Message message, NetSession<?> session, MethodControllerHolder controller) {
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
            MethodControllerHolder controller = this.controller;
            if (controller == null)
                return String.valueOf(this.message.getProtocol());
            else
                return controller.getClass() + controller.getName();
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

        private void handleResult(ResultCode code, Object value, Throwable cause, boolean mustSend) {
            if (callback != null) {
                try {
                    callback.callback(code, value, cause);
                } catch (Throwable e) {
                    DISPATCHER_LOG.error("Controller [{}] 执行回调方法 {} 异常", getName(), callback.getClass(), e);
                }
            }
            MessageContent<?> content = null;
            switch (message.getMode()) {
                case PUSH:
                    if (mustSend || code.isSuccess())
                        content = MessageContent.toPush(code, value);
                    break;
                case REQUEST:
                    content = MessageContent.toResponse(code, value, message.getToMessage());
                    break;
            }

            if (content != null) {
                if (code.getType() != ResultCodeType.ERROR) {
                    this.session.sendMessage(this.message, content);
                } else {
                    this.session.sendMessage(this.message, content
                            .setSentHandler((session, message) -> session.offline(true)));
                }
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
                DISPATCHER_LOG.error("Controller [{}] 处理消息异常 {} - {} ", getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
            } finally {
                this.executed = true;
            }
            return result;
        }

        @Override
        public void execute() {
            CurrentCommand.setCurrent(this.message.getUserID(), this.message.getProtocol());
            if (done)
                return;
            if (!executed) {
                CommandResult result = invoke();
                Object value = result.getBody();
                this.commandFuture = getCommandFuture(value);
                if (this.commandFuture == null) {
                    try {
                        this.handleResult(result.getResultCode(), result.getBody(), null, true);
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
                            this.handleResult(ResultCode.SUCCESS, value, null, true);
                        } else {
                            CommandResult result = handleDispatchException(this.commandFuture.getCause());
                            DISPATCHER_LOG.error("Controller [{}] 轮询Command结束异常 {} - {} ", getName(), result.getResultCode(), result.getResultCode().getMessage(), this.commandFuture.getCause());
                            this.handleResult(result.getResultCode(), result.getBody(), this.commandFuture.getCause(), false);
                        }
                    } finally {
                        this.done = true;
                    }
                } catch (Throwable e) {
                    try {
                        CommandResult result = handleDispatchException(e);
                        DISPATCHER_LOG.error("Controller [{}] 轮询Command结束异常 {} - {} ", getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
                        this.handleResult(result.getResultCode(), result.getBody(), e, false);
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
                CommonMessageDispatcher.DISPATCHER_LOG.error(dex.getMessage(), dex);
                DispatchExceptionEvent event = new DispatchExceptionEvent(this.message, controller, dex);
                CommonMessageDispatcher.this.fireExecuteDispatchException(event);
                if (event.isInterrupt())
                    result = getEventResult(this.message.getMode(), event);
                else
                    result = ResultFactory.create(dex.getResultCode(), null);
            } else if (e instanceof InvocationTargetException) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
                Throwable ex = ((InvocationTargetException) e).getTargetException();
                result = this.handleDispatchException(ex);
            } else {
                CommonMessageDispatcher.DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
                DispatchMessageErrorEvent event = new DispatchMessageErrorEvent(this.message, controller, e);
                CommonMessageDispatcher.this.fireExecuteException(event);
                if (event.isInterrupt())
                    result = getEventResult(this.message.getMode(), event);
                else
                    return ResultFactory.create(CoreResponseCode.EXECUTE_EXCEPTION, null);
            }
            return result;
        }

        private CommandResult getEventResult(MessageMode mode, ExecuteMessageEvent event) {
            return getResult(mode, event.getResult());
        }

        private CommandResult getResult(MessageMode mode, CommandResult result) {
            if (result != null)
                return result;
            return mode == MessageMode.REQUEST ? ResultFactory.success() : CommandResult.NO_RESULT;
        }

        private CommandResult doExecute() throws Exception {
            ExecuteMessageEvent event;
            MessageMode mode = message.getMode();

            try {
                event = new ExecuteMessageEvent(this.message, controller);
                CommonMessageDispatcher.this.fireReceive(event);
                if (event.isInterrupt())
                    return getEventResult(mode, event);

                if (this.controller == null) {
                    CommonMessageDispatcher.DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", message.getProtocol());
                    return getResult(mode, null);
                }

                if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                    CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 开始处理Message : {}\n ", getName(), message.toString());

                this.checkExecutable(controller);

                // 调用方法
                if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                    CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 触Message处理事件", getName());
                event = new ExecuteMessageEvent(this.message, controller);
                CommonMessageDispatcher.this.fireExecute(event);
                if (event.isInterrupt())
                    return getEventResult(mode, event);

                if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                    CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
                CommandResult result = controller.execute(this.session, this.message);

                // 执行结束触发命令执行完成事件
                if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                    CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 触发Message处理完成事件", getName());
                event = new ExecuteMessageEvent(this.message, controller, result);
                CommonMessageDispatcher.this.fireExecuteFinish(event);

                if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                    CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());

                if (event.isInterrupt())
                    return getEventResult(mode, event);
                else
                    return result;
            } finally {
                CommonMessageDispatcher.this.fireDispatchFinish(new DispatchMessageEvent(message, controller));
            }
        }

        @SuppressWarnings("unchecked")
        private void checkExecutable(MethodControllerHolder controller) throws DispatchException {


            // 是否需要做登录校验,判断是否已经登录
            NetSessionHolder<?, NetSession<?>> sessionHolder = this.appContext.getSessionHolder();

            if (controller.isAuth() && !this.session.isLogin()) {
                if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                    CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
                List<AuthProvider> authProviders = this.appContext.getAuthProviders();
                for (AuthProvider provider : authProviders) {
                    if (!provider.isCanValidate(this.message))
                        continue;
                    LoginCertificate loginInfo = provider.validate(this.message);
                    Optional<NetSession<?>> onlineOpt;
                    if (loginInfo == null || !loginInfo.isLogin() || !(onlineOpt = sessionHolder.online(this.session, loginInfo)).isPresent()) {
                        CommonMessageDispatcher.DISPATCHER_LOG.error("Controller [{}] 用户未登陆", getName());
                        throw new DispatchException(CoreResponseCode.UNLOGIN);
                    } else {
                        this.session = onlineOpt.get();
                        break;
                    }
                }
            }

            if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", getName());
            if (!controller.isUserGroup(this.session.getGroup())) {
                CommonMessageDispatcher.DISPATCHER_LOG.error("Controller [{}] 用户为[{}]用户组, 无法调用此协议", getName(), this.session.getGroup());
                throw new DispatchException(CoreResponseCode.NO_PERMISSIONS);
            }

            if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                CommonMessageDispatcher.DISPATCHER_LOG.debug("Controller [{}] 进行Checker检验", getName());

            List<CheckerHolder> checkers = controller.getCheckerHolders();//this.session.getCheckers();
            if (!checkers.isEmpty()) {
                // 检测消息的正确性
                for (CheckerHolder checker : checkers) {
                    if (CommonMessageDispatcher.DISPATCHER_LOG.isDebugEnabled())
                        CommonMessageDispatcher.DISPATCHER_LOG.debug("\tController [{}] Controller [{}] 进行Checker {} 检验", getName(), checker.getClass());
                    // 获取普通调用的校验码密钥
                    ResultCode resultCode;
                    if ((resultCode = checker.check(this.session, message, appContext)).isFailure()) {
                        CommonMessageDispatcher.DISPATCHER_LOG.error("Controller [{}] 进行Checker {} 检验失败原因: {} - {}", getName(), checker.getClass(), resultCode, resultCode.getMessage());
                        throw new DispatchException(resultCode);
                    }
                }
            }
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

}

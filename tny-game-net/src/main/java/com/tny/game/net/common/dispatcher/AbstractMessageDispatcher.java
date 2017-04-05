package com.tny.game.net.common.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.actor.Answer;
import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.stage.StageUtils;
import com.tny.game.actor.stage.TaskStage;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodeType;
import com.tny.game.common.utils.collection.CollectUtils;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.net.annotation.AuthProtocol;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.DispatchCommand;
import com.tny.game.net.command.MessageDispatcher;
import com.tny.game.net.command.listener.DispatchCommandEvent;
import com.tny.game.net.command.listener.DispatchCommandExceptionEvent;
import com.tny.game.net.command.listener.DispatchCommandExecuteEvent;
import com.tny.game.net.command.listener.DispatchCommandListener;
import com.tny.game.net.common.ControllerCheckerHolder;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.LoginCertificate;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.holder.NetSessionHolder;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.worker.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
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
public abstract class AbstractMessageDispatcher implements MessageDispatcher {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    /**
     * Controller Map
     */
    private Map<Integer, Map<MessageMode, MethodControllerHolder>> methodHolder = new ConcurrentHashMap<>();

    private AuthProvider authAllProvider;

    /**
     *
     */
    private Map<Class<?>, ControllerPlugin> pluginMap = new CopyOnWriteMap<>();

    /**
     * 派发错误监听器
     */
    private final List<DispatchCommandListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * 认证器列表
     */
    private final Map<Object, AuthProvider> authProviders = new CopyOnWriteMap<>();

    /**
     * 检测器Map
     */
    private final Map<Class<?>, ControllerChecker> checkerMap = new CopyOnWriteMap<>();

    private AppConfiguration appConfiguration;

    public AbstractMessageDispatcher(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public DispatchCommand<CommandResult> dispatch(Message message, MessageFuture<?> future, Tunnel<?> tunnel) throws DispatchException {
        // 获取方法持有器
        MethodControllerHolder controller = null;
        final Map<MessageMode, MethodControllerHolder> controllerMap = this.methodHolder.get(message.getProtocol());
        if (controllerMap != null)
            controller = controllerMap.get(message.getMode());
        return new MessageDispatchCommand(message, future, tunnel, controller);

    }

    @Override
    public void addDispatcherRequestListener(final DispatchCommandListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void addDispatcherRequestListener(final Collection<DispatchCommandListener> listeners) {
        this.listeners.addAll(listeners);
    }

    @Override
    public void removeDispatcherRequestListener(final DispatchCommandListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void clearDispatcherRequestListener() {
        this.listeners.clear();
    }

    /**
     * 触发接收消息消息事件
     * <p>
     * <p>
     * 触发接收消息消息事件<br>
     *
     * @param event 接收消息
     */
    private void fireReceive(DispatchCommandExecuteEvent event) {
        for (DispatchCommandListener listener : this.listeners) {
            try {
                listener.receive(event);
            } catch (Exception e) {
                DISPATCHER_LOG.error("处理 {}.receive", listener.getClass(), e);
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
    private void fireExecuteException(DispatchCommandExceptionEvent event) {
        for (DispatchCommandListener listener : this.listeners) {
            try {
                listener.executeException(event);
            } catch (Exception e) {
                DISPATCHER_LOG.error("处理 {}.executeException 监听器异常", listener.getClass(), e);
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
    private void fireExecute(DispatchCommandExecuteEvent event) {
        for (DispatchCommandListener listener : this.listeners) {
            try {
                listener.execute(event);
            } catch (Exception e) {
                DISPATCHER_LOG.error("处理 {}.execute 监听器异常", listener.getClass(), e);
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
    private void fireExecuteFinish(DispatchCommandExecuteEvent event) {
        for (DispatchCommandListener listener : this.listeners) {
            try {
                listener.executeFinish(event);
            } catch (Exception e) {
                DISPATCHER_LOG.error("处理 {}.executeFinish 监听器异常", listener.getClass(), e);
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
    private void fireDispatchFinish(DispatchCommandEvent event) {
        for (DispatchCommandListener listener : this.listeners) {
            try {
                listener.dispatchFinish(event);
            } catch (Exception e) {
                DISPATCHER_LOG.error("处理 {}.dispatchFinish 监听器异常", listener.getClass(), e);
            }
        }
    }

    protected ControllerPlugin getPlugin(Class<?> pluginClass) {
        return this.pluginMap.get(pluginClass);
    }

    protected void addControllerPlugin(Collection<ControllerPlugin> plugins) {
        this.pluginMap.putAll(plugins.stream()
                .collect(CollectUtils.toMap(ControllerPlugin::getClass)));
    }

    protected void addControllerPlugin(ControllerPlugin plugin) {
        this.pluginMap.put(plugin.getClass(), plugin);
    }

    protected void addControllerChecker(Collection<ControllerChecker> checkers) {
        this.checkerMap.putAll(checkers.stream()
                .collect(CollectUtils.toMap(ControllerChecker::getClass)));
    }

    protected ControllerChecker getChecker(Class<?> checkerClass) {
        return this.checkerMap.get(checkerClass);
    }

    protected void addControllerChecker(ControllerChecker checker) {
        this.checkerMap.put(checker.getClass(), checker);
    }

    protected void addAuthProvider(Collection<AuthProvider> providers) {
        providers.forEach(this::addAuthProvider);
    }

    protected void addAuthProvider(AuthProvider provider) {
        Class<?> providerClass = provider.getClass();
        AuthProtocol protocol = providerClass.getAnnotation(AuthProtocol.class);
        if (protocol != null) {
            if (protocol.all()) {
                ExceptionUtils.checkNotNull(this.authAllProvider, "添加 {} 失败! 存在全局AuthProvider {}", providerClass, authAllProvider.getClass());
                this.authAllProvider = provider;
            } else {
                for (int value : protocol.value()) {
                    putObject(this.authProviders, value, provider);
                }
            }
        }
        putObject(this.authProviders, provider.getClass(), provider);
    }

    private <K, V> void putObject(Map<K, V> map, K key, V value) {
        V oldValue = map.put(key, value);
        if (oldValue != null)
            ExceptionUtils.throwException(IllegalArgumentException::new,
                    "添加 {} 失败! key {} 存在 {} 对象", value.getClass(), key, oldValue.getClass());
    }

    protected void addController(Collection<Object> objects) {
        objects.forEach(this::addController);
    }

    protected void addController(Object object) {
        NetSessionHolder sessionHolder = appConfiguration.getSessionHolder();
        if (sessionHolder == null)
            throw new NullPointerException("sessionHolder is null");
        Map<Integer, Map<MessageMode, MethodControllerHolder>> methodHolder = this.methodHolder;
        final ClassControllerHolder holder = new ClassControllerHolder(object, this);
        for (Entry<Integer, MethodControllerHolder> entry : holder.getMethodHolderMap().entrySet()) {
            MethodControllerHolder controller = entry.getValue();
            Map<MessageMode, MethodControllerHolder> holderMap = methodHolder.computeIfAbsent(controller.getID(), CopyOnWriteMap::new);
            for (MessageMode mode : controller.getMessageModes()) {
                MethodControllerHolder old = holderMap.putIfAbsent(mode, controller);
                if (old != null)
                    throw new IllegalArgumentException(LogUtils.format("{} 与 {} 对MessageMode {} 处理发生冲突", old, controller, mode));
            }
        }
    }

    private class MessageDispatchCommand implements DispatchCommand<CommandResult> {

        private Tunnel<?> tunnel;

        private Message<Object> message;

        private MessageFuture<Object> messageFuture;

        private MethodControllerHolder controller;

        // private CommandFuture commandFuture;

        private Callback<Object> callback;

        private boolean executed;

        private boolean done;

        @SuppressWarnings("unchecked")
        private MessageDispatchCommand(Message message, MessageFuture messageFuture, Tunnel tunnel, MethodControllerHolder controller) {
            this.message = message;
            this.messageFuture = messageFuture;
            this.tunnel = tunnel;
            this.controller = controller;
        }

        @SuppressWarnings("unchecked")
        @Override
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

        private void handleResult(CommandResult result, Throwable cause, boolean mustSend) {
            ResultCode code = result == null ? ResultCode.SUCCESS : result.getResultCode();
            Object value = result == null ? null : result.getBody();
            handleResult(code, value, cause, mustSend);
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
                        content = MessageContent.toPush(this.message, code, value);
                    break;
                case REQUEST:
                    content = MessageContent.toResponse(this.message, code, value, message.getID());
                    break;
            }

            if (content != null) {
                if (code.getType() != ResultCodeType.ERROR) {
                    this.tunnel.send(content);
                } else {
                    content.createSentFuture()
                            .getSendFuture()
                            .whenComplete((tunnel, e) -> tunnel.close());
                    this.tunnel.send(content);
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
                if (result != null)
                    this.handleResult(result, null, true);
            } catch (Throwable e) {
                result = this.handleException(e);
                DISPATCHER_LOG.error("Controller [{}] 处理消息异常 {} - {} ", getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
            } finally {
                this.executed = true;
                if (this.messageFuture != null) {
                    this.messageFuture.complete(this.message);
                }
            }
            return result;
        }

        @Override
        public void execute() {
            CurrentCommand.setCurrent(this.message.getUserID(), this.message.getProtocol());
            invoke();
            // if (done)
            //     return;
            // if (!executed) {
            // CommandResult result = invoke();
            // Object value = null;
            // if (result != null)
            // value = result.getBody();
            // this.commandFuture = getCommandFuture(value);
            // if (this.commandFuture == null) {
            //     try {
            // this.handleResult(result, null, true);
            //     } finally {
            //         this.done = true;
            //     }
            // }
            // }
            // if (!done && this.commandFuture != null && this.commandFuture.isDone()) {
            //     try {
            //         try {
            //             if (this.commandFuture.isSuccess()) {
            //                 Object value = this.commandFuture.getResult();
            //                 if (value instanceof CommandResult)
            //                     this.handleResult((CommandResult) value, null, true);
            //                 else
            //                     this.handleResult(ResultCode.SUCCESS, value, null, true);
            //             } else {
            //                 CommandResult result = handleException(this.commandFuture.getCause());
            //                 DISPATCHER_LOG.error("Controller [{}] 轮询Command结束异常 {} - {} ", getName(), result.getResultCode(), result.getResultCode().getMessage(), this.commandFuture.getCause());
            //                 this.handleResult(result, this.commandFuture.getCause(), false);
            //             }
            //         } finally {
            //             this.done = true;
            //         }
            //     } catch (Throwable e) {
            //         try {
            //             CommandResult result = handleException(e);
            //             DISPATCHER_LOG.error("Controller [{}] 轮询Command结束异常 {} - {} ", getName(), result.getResultCode(), result.getResultCode().getMessage(), e);
            //             this.handleResult(result, e, false);
            //         } finally {
            //             this.done = true;
            //         }
            //     }
            // }
        }


        @SuppressWarnings("unchecked")
        private CommandResult handleException(Throwable e) {
            CommandResult result;
            if (e instanceof DispatchException) {
                DispatchException dex = (DispatchException) e;
                DISPATCHER_LOG.error(dex.getMessage(), dex);
                DispatchCommandExceptionEvent event = new DispatchCommandExceptionEvent(this, this.tunnel, this.message, controller, dex);
                AbstractMessageDispatcher.this.fireExecuteException(event);
                if (event.isInterrupt())
                    result = getEventResult(this.message.getMode(), event);
                else
                    result = ResultFactory.create(dex.getResultCode(), null);
            } else if (e instanceof InvocationTargetException) {
                DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
                Throwable ex = ((InvocationTargetException) e).getTargetException();
                result = this.handleException(ex);
            } else {
                DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
                DispatchCommandExceptionEvent event = new DispatchCommandExceptionEvent(this, this.tunnel, this.message, controller, e);
                AbstractMessageDispatcher.this.fireExecuteException(event);
                if (event.isInterrupt())
                    result = getEventResult(this.message.getMode(), event);
                else
                    return ResultFactory.create(CoreResponseCode.EXECUTE_EXCEPTION, null);
            }
            return result;
        }

        private CommandResult getEventResult(MessageMode mode, DispatchCommandExecuteEvent event) {
            return getResult(mode, event.getResult());
        }

        private CommandResult getResult(MessageMode mode, CommandResult result) {
            if (result != null)
                return result;
            return mode == MessageMode.REQUEST ? ResultFactory.success() : CommandResult.NO_RESULT;
        }

        @SuppressWarnings("unchecked")
        private CommandResult doExecute() throws Exception {
            DispatchCommandExecuteEvent event;
            MessageMode mode = message.getMode();

            try {
                //检测认证
                this.checkAuth();

                event = new DispatchCommandExecuteEvent(this, this.tunnel, this.message, controller);
                AbstractMessageDispatcher.this.fireReceive(event);
                if (event.isInterrupt())
                    return getEventResult(mode, event);

                if (this.controller == null) {
                    DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", message.getProtocol());
                    return getResult(mode, null);
                }

                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 开始处理Message : {}\n ", getName(), message.toString());

                // 检测是否controller运行条件
                this.checkController();

                // 调用方法
                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 触Message处理事件", getName());
                event = new DispatchCommandExecuteEvent(this, this.tunnel, this.message, controller);
                AbstractMessageDispatcher.this.fireExecute(event);
                if (event.isInterrupt())
                    return getEventResult(mode, event);

                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
                CommandResult result = controller.execute(this.tunnel, this.message);

                // 执行结束触发命令执行完成事件
                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 触发Message处理完成事件", getName());
                event = new DispatchCommandExecuteEvent(this, this.tunnel, this.message, controller, result);
                AbstractMessageDispatcher.this.fireExecuteFinish(event);

                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());

                if (event.isInterrupt())
                    return getEventResult(mode, event);
                else
                    return result;
            } finally {
                fireDispatchFinish(new DispatchCommandEvent(this, this.tunnel, this.message, controller));
            }
        }

        @SuppressWarnings("unchecked")
        private void checkAuth() throws DispatchException {
            if (!this.tunnel.isLogin()) {
                AuthProvider provider = null;
                if (this.controller != null) {
                    Class<?> clazz = controller.getAuthProvider();
                    if (clazz != null) {
                        provider = authProviders.get(clazz);
                        ExceptionUtils.checkNotNull(provider, "{} 认证器不存在", clazz);
                    }
                }
                if (provider == null)
                    provider = authProviders.getOrDefault(message.getProtocol(), authAllProvider);
                if (provider == null)
                    return;
                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
                LoginCertificate loginInfo = provider.validate(this.tunnel, this.message);
                // 是否需要做登录校验,判断是否已经登录
                NetSessionHolder sessionHolder = appConfiguration.getSessionHolder();
                if (loginInfo != null && loginInfo.isLogin()) {
                    sessionHolder.online((NetSession<?>) this.tunnel.getSession(), loginInfo);
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void checkController() throws DispatchException {


            // @TODO 移到MessageChecker
            // String serverType = appContext.getScopeType();
            // if (serverType != null && !methodHolder.isCanCall(serverType)) {
            //     NetMessageDispatcher.DISPATCHER_LOG.error("{} 服务器无法调用此协议", LogUtils.msg(serverType, methodHolder.getMethodClass(), methodHolder.getName()));
            //     throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
            // }
            String appType = appConfiguration.getAppType();
            if (!controller.isActive(appType)) {
                DISPATCHER_LOG.error("Controller [{}] 应用类型 {} 无法此协议", getName(), appType);
                throw new DispatchException(CoreResponseCode.NO_SUCH_PROTOCOL);
            }

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
            if (controller.isAuth() && !this.tunnel.isLogin()) {
                DISPATCHER_LOG.error("Controller [{}] 用户未登陆", getName());
            }

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", getName());
            if (!controller.isUserGroup(this.tunnel.getUserGroup())) {
                DISPATCHER_LOG.error("Controller [{}] 用户为[{}]用户组, 无法调用此协议", getName(), this.tunnel.getUserGroup());
                throw new DispatchException(CoreResponseCode.NO_PERMISSIONS);
            }

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 进行Checker检验", getName());

            List<ControllerCheckerHolder> checkers = controller.getCheckerHolders();//this.session.getCheckers();
            if (!checkers.isEmpty()) {
                // 检测消息的正确性
                for (ControllerCheckerHolder checker : checkers) {
                    if (DISPATCHER_LOG.isDebugEnabled())
                        DISPATCHER_LOG.debug("\tController [{}] Controller [{}] 进行Checker {} 检验", getName(), checker.getClass());
                    // 获取普通调用的校验码密钥
                    ResultCode resultCode;
                    if ((resultCode = checker.check(this.tunnel, message)).isFailure()) {
                        DISPATCHER_LOG.error("Controller [{}] 进行Checker {} 检验失败原因: {} - {}", getName(), checker.getClass(), resultCode, resultCode.getMessage());
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

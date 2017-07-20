package com.tny.game.net.common.dispatcher;

import com.tny.game.common.collection.CollectorsAide;
import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.concurrent.Waitable;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodeType;
import com.tny.game.common.utils.Logs;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.common.utils.Throws;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.annotation.AuthProtocol;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.DispatchContext;
import com.tny.game.net.command.listener.DispatchCommandExecuteListener;
import com.tny.game.net.command.listener.DispatchCommandInvokeListener;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    private final List<DispatchCommandExecuteListener> executeListeners = new CopyOnWriteArrayList<>();

    /**
     * 派发错误监听器
     */
    private final List<DispatchCommandInvokeListener> invokeListeners = new CopyOnWriteArrayList<>();

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
    public Command dispatch(Message message, MessageFuture<?> future, Tunnel<?> tunnel) throws DispatchException {
        // 获取方法持有器
        MethodControllerHolder controller = null;
        final Map<MessageMode, MethodControllerHolder> controllerMap = this.methodHolder.get(message.getProtocol());
        if (controllerMap != null)
            controller = controllerMap.get(message.getMode());
        return new MessageDispatchCommand(message, future, tunnel, controller);

    }

    @Override
    public void addListener(final DispatchCommandListener listener) {
        if (listener instanceof DispatchCommandInvokeListener)
            this.invokeListeners.add((DispatchCommandInvokeListener) listener);
        if (listener instanceof DispatchCommandExecuteListener)
            this.executeListeners.add((DispatchCommandExecuteListener) listener);
    }

    @Override
    public void addListener(final Collection<DispatchCommandListener> listeners) {
        listeners.forEach(this::addListener);
    }

    @Override
    public void removeListener(final DispatchCommandListener listener) {
        if (listener instanceof DispatchCommandInvokeListener)
            this.invokeListeners.remove(listener);
        if (listener instanceof DispatchCommandExecuteListener)
            this.executeListeners.remove(listener);
    }

    @Override
    public void clearListener() {
        this.invokeListeners.clear();
        this.executeListeners.clear();
    }

    /**
     * 触发接将要运行业务事件
     *
     * @param context 派发上下文
     */
    private void fireWillInvoke(DispatchContext context) {
        for (DispatchCommandInvokeListener listener : this.invokeListeners) {
            try {
                listener.willInvoke(context);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.willInvoke 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行事件
     *
     * @param context 派发上下文
     */
    private void fireInvoke(DispatchContext context) {
        for (DispatchCommandInvokeListener listener : this.invokeListeners) {
            try {
                listener.invoke(context);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.invoke 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发业务运行事件成功
     *
     * @param context 派发上下文
     */
    private void fireInvoked(DispatchContext context) {
        for (DispatchCommandInvokeListener listener : this.invokeListeners) {
            try {
                listener.invoked(context);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.invoked 监听器异常", listener.getClass(), e);
            }
        }
    }


    /**
     * 触发业务运行完成事件, 无论成功失败
     *
     * @param context 派发上下文
     * @param cause   失败原因, 成功为null
     */
    private void fireInvokeFinish(DispatchContext context, Throwable cause) {
        for (DispatchCommandInvokeListener listener : this.invokeListeners) {
            try {
                listener.invokeFinish(context, cause);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.invokeFinish 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发调用执行错误事件
     *
     * @param context 派发上下文
     * @param cause   失败原因
     */
    private void fireInvokeException(DispatchContext context, Throwable cause) {
        for (DispatchCommandInvokeListener listener : this.invokeListeners) {
            try {
                listener.invokeException(context, cause);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.invokeException 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发开始Command执行
     *
     * @param context 派发上下文
     */
    private void fireExecute(DispatchContext context) {
        for (DispatchCommandExecuteListener listener : this.executeListeners) {
            try {
                listener.execute(context);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.execute 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发开始Command执行完成
     *
     * @param context 派发上下文
     */
    private void fireExecuted(DispatchContext context) {
        for (DispatchCommandExecuteListener listener : this.executeListeners) {
            try {
                listener.executed(context);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.executed 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发Command执行完成, 无论成功还是失败
     *
     * @param context 派发上下文
     * @param cause   失败原因
     */
    private void fireExecuteFinish(DispatchContext context, Throwable cause) {
        for (DispatchCommandExecuteListener listener : this.executeListeners) {
            try {
                listener.executeFinish(context, cause);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.executeFinish 监听器异常", listener.getClass(), e);
            }
        }
    }

    /**
     * 触发Command执行错误事件
     *
     * @param context 派发上下文
     * @param cause   失败原因
     */
    private void fireExecuteException(DispatchContext context, Throwable cause) {
        for (DispatchCommandExecuteListener listener : this.executeListeners) {
            try {
                listener.executeException(context, cause);
            } catch (Throwable e) {
                DISPATCHER_LOG.error("处理 {}.executeException 监听器异常", listener.getClass(), e);
            }
        }
    }


    protected ControllerPlugin getPlugin(Class<?> pluginClass) {
        return this.pluginMap.get(pluginClass);
    }

    protected void addControllerPlugin(Collection<ControllerPlugin> plugins) {
        this.pluginMap.putAll(plugins.stream()
                .collect(CollectorsAide.toMap(ControllerPlugin::getClass)));
    }

    protected void addControllerPlugin(ControllerPlugin plugin) {
        this.pluginMap.put(plugin.getClass(), plugin);
    }

    protected void addControllerChecker(Collection<ControllerChecker> checkers) {
        this.checkerMap.putAll(checkers.stream()
                .collect(CollectorsAide.toMap(ControllerChecker::getClass)));
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
                Throws.checkNotNull(this.authAllProvider, "添加 {} 失败! 存在全局AuthProvider {}", providerClass, authAllProvider.getClass());
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
            Throws.throwException(IllegalArgumentException::new,
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
                    throw new IllegalArgumentException(Logs.format("{} 与 {} 对MessageMode {} 处理发生冲突", old, controller, mode));
            }
        }
    }

    private class MessageDispatchCommand extends DispatchContext implements Command {

        private MessageFuture<Object> messageFuture;

        private Waitable<Object> waitable;

        private long timeout;

        private boolean done;

        @SuppressWarnings("unchecked")
        private MessageDispatchCommand(Message message, MessageFuture messageFuture, Tunnel tunnel, MethodControllerHolder controller) {
            super(controller, tunnel, message);
            this.messageFuture = messageFuture;
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
            return interrupted || done;
        }

        /* 检测结果 */
        private void checkResult(Throwable cause) {
            Object o = this.result;
            if (o instanceof Waitable) { // 如果是结果为Future进入等待逻辑
                this.waitable = ObjectAide.as(o);
                this.timeout = System.currentTimeMillis() + 5000; // 超时
                this.result = null;
            } else if (o instanceof Future) { // 如果是结果为Future进入等待逻辑
                this.waitable = Waitable.of(ObjectAide.as(o));
                this.timeout = System.currentTimeMillis() + 5000; // 超时
                this.result = null;
            } else { // 结果处理逻辑
                this.postInvoke();
                this.done = true;
                this.handleResult(cause);
            }
        }

        /* 检测是否有future对象*/
        private void checkFuture() throws Throwable {
            if (this.waitable != null) { // 检测是否
                if (this.waitable.isDone()) {
                    if (this.waitable.isSuccess()) {
                        this.result = this.waitable.getResult();
                        this.checkResult(null);
                    } else {
                        this.result = null;
                        throw this.waitable.getCause();
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void handleResult(Throwable cause) {
            if (!this.isDone())
                return;
            try {
                MessageContent<?> content = null;
                ResultCode code = ResultCode.SUCCESS;
                Object value = null;
                if (this.result instanceof CommandResult) {
                    CommandResult commandResult = (CommandResult) this.result;
                    code = commandResult.getResultCode();
                    value = commandResult.getBody();
                } else if (this.result instanceof ResultCode) {
                    code = (ResultCode) this.result;
                } else {
                    value = this.result;
                }
                if (cause != null)
                    DISPATCHER_LOG.error("Controller [{}] 处理消息异常 {} - {} ", getName(), code, code.getCode(), cause);
                boolean mustSend = this.result != null;
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
                        content.sendCompletionStage()
                                .whenComplete((tunnel, e) -> tunnel.close());
                        this.tunnel.send(content);
                    }
                }
            } finally {
                if (this.messageFuture != null) {
                    this.messageFuture.complete(this.message);
                }
            }
        }


        @Override
        public void execute() {
            CurrentCommand.setCurrent(this.message.getUserID(), this.message.getProtocol());
            try {
                if (isDone())
                    return;
                fireExecute(this);
                if (waitable == null) {
                    // 调用逻辑业务
                    this.invoke();
                    // 调用调用结果
                    this.checkResult(null);
                }
                if (!this.isDone())
                    this.checkFuture();
                fireExecuted(this);
                fireExecuteFinish(this, null);
            } catch (Throwable e) {
                this.handleException(e);
                this.checkResult(e);
                fireExecuteException(this, e);
                fireExecuteFinish(this, e);
            }
        }


        @SuppressWarnings("unchecked")
        private void handleException(Throwable e) {
            if (e instanceof DispatchException) {
                // DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
                DispatchException dex = (DispatchException) e;
                DISPATCHER_LOG.error(dex.getMessage(), dex);
                fireInvokeException(this, dex);
                if (!this.isInterrupted())
                    this.done(dex.getResultCode());
                fireInvokeFinish(this, e);
            } else if (e instanceof InvocationTargetException) {
                this.handleException(((InvocationTargetException) e).getTargetException());
            } else if (e instanceof ExecutionException) {
                this.handleException(e.getCause());
            } else {
                // DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
                fireInvokeException(this, e);
                if (!this.isInterrupted())
                    this.done(CoreResponseCode.EXECUTE_EXCEPTION);
                fireInvokeFinish(this, e);
            }
        }

        private CommandResult getResult(MessageMode mode, CommandResult result) {
            if (result != null)
                return result;
            return mode == MessageMode.REQUEST ? ResultFactory.success() : CommandResult.NO_RESULT;
        }

        @SuppressWarnings("unchecked")
        private void invoke() throws Exception {

            //检测认证
            this.checkAuth();

            fireWillInvoke(this);
            if (this.isInterrupted())
                return;

            if (this.controller == null) {
                DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", message.getProtocol());
                this.done(CoreResponseCode.NO_SUCH_PROTOCOL);
                return;
            }

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 开始处理Message : {}\n ", getName(), message.toString());

            // 检测是否controller运行条件
            this.checkController();

            // 调用方法
            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 触Message处理事件", getName());
            fireInvoke(this);
            if (this.isInterrupted())
                return;

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 执行BeforePlugins", getName());
            controller.beforeInvoke(this.tunnel, this.message, this);

            if (this.isInterrupted())
                return;

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
            controller.invoke(this.tunnel, this.message, this);

            fireInvoked(this);
            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 执行业务成功!", getName());
        }

        @SuppressWarnings("unchecked")
        private void postInvoke() {
            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 执行AftertPlugins", getName());
            controller.afterInvke(this.tunnel, this.message, this);

            // 执行结束触发命令执行完成事件
            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 触发Message处理完成事件", getName());
            fireInvokeFinish(this, null);

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());
        }

        @SuppressWarnings("unchecked")
        private void checkAuth() throws DispatchException {
            if (!this.tunnel.isLogin()) {
                AuthProvider provider = null;
                if (this.controller != null) {
                    Class<?> clazz = controller.getAuthProvider();
                    if (clazz != null) {
                        provider = authProviders.get(clazz);
                        Throws.checkNotNull(provider, "{} 认证器不存在", clazz);
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

}

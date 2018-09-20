package com.tny.game.net.command.dispatcher;

import com.google.common.base.MoreObjects;
import com.tny.game.common.collection.*;
import com.tny.game.common.concurrent.Waiter;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.Command;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.annotation.AuthProtocol;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.AuthenticateProvider;
import com.tny.game.net.command.checker.ControllerChecker;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.common.ControllerCheckerHolder;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 抽象消息派发器
 *
 * @author KGTny
 * <p>
 * 抽象消息派发器
 * <p>
 * <p>
 * 实现对controllerMap的初始化,派发消息流程<br>
 */
public abstract class AbstractMessageDispatcher implements MessageDispatcher {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    /**
     * Controller Map
     */
    private Map<Integer, Map<MessageMode, MethodControllerHolder>> methodHolder = new ConcurrentHashMap<>();

    private AuthenticateProvider authAllProvider;

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
    private final Map<Object, AuthenticateProvider> authProviders = new CopyOnWriteMap<>();

    /**
     * 检测器Map
     */
    private final Map<Class<?>, ControllerChecker> checkerMap = new CopyOnWriteMap<>();

    private AppConfiguration appConfiguration;

    public AbstractMessageDispatcher(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public Command dispatch(MessageReceiveEvent<?> event) {
        // 获取方法持有器
        MethodControllerHolder controller = null;
        Message<?> message = event.getMessage();
        MessageHeader header = message.getHeader();
        final Map<MessageMode, MethodControllerHolder> controllerMap = this.methodHolder.get(header.getProtocol());
        if (controllerMap != null)
            controller = controllerMap.get(message.getMode());
        return new MessageDispatchCommand(controller, event);

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

    protected void addAuthProvider(Collection<AuthenticateProvider> providers) {
        providers.forEach(this::addAuthProvider);
    }

    protected void addAuthProvider(AuthenticateProvider provider) {
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
        ExprHolderFactory exprHolderFactory = appConfiguration.getExprHolderFactory();
        Map<Integer, Map<MessageMode, MethodControllerHolder>> methodHolder = this.methodHolder;
        final ClassControllerHolder holder = new ClassControllerHolder(object, this, exprHolderFactory);
        for (Entry<Integer, MethodControllerHolder> entry : holder.getMethodHolderMap().entrySet()) {
            MethodControllerHolder controller = entry.getValue();
            Map<MessageMode, MethodControllerHolder> holderMap = methodHolder.computeIfAbsent(controller.getID(), CopyOnWriteMap::new);
            for (MessageMode mode : controller.getMessageModes()) {
                MethodControllerHolder old = holderMap.putIfAbsent(mode, controller);
                if (old != null)
                    throw new IllegalArgumentException(format("{} 与 {} 对MessageMode {} 处理发生冲突", old, controller, mode));
            }
        }
    }

    private class MessageDispatchCommand extends DispatchContext implements Command {

        private Waiter<Object> waiter;

        private long timeout;

        private boolean done;

        @SuppressWarnings("unchecked")
        private MessageDispatchCommand(MethodControllerHolder controller, MessageReceiveEvent<?> event) {
            super(controller, event);
        }

        @Override
        public String getName() {
            MethodControllerHolder controller = this.controller;
            if (controller == null) {
                MessageHeader header = message.getHeader();
                return String.valueOf(header.getProtocol());
            } else {
                return controller.getControllerClass() + "." + controller.getName();
            }
        }

        @Override
        public boolean isDone() {
            return intercept || done;
        }

        @Override
        public void execute() {
            MessageHeader header = message.getHeader();
            CurrentCommand.setCurrent(this.message.getUserID(), header.getProtocol());
            try {
                if (isDone())
                    return;
                fireExecute(this);
                if (waiter == null) {
                    // 调用逻辑业务
                    this.invoke();
                    // 调用调用结果
                    this.checkResult(null);
                }
                if (!this.isDone()) {
                    this.checkWaiter();
                }
                fireExecuted(this);
            } catch (Throwable e) {
                this.handleException(e);
                this.checkResult(e);
                fireExecuteException(this, e);
            } finally {
                fireExecuteFinish(this, null);
            }
        }

        /* 检测结果 */
        private void checkResult(Throwable cause) {
            Object o = this.result;
            if (o instanceof Waiter) { // 如果是结果为Future进入等待逻辑
                this.waiter = ObjectAide.as(o);
                this.timeout = System.currentTimeMillis() + 5000; // 超时
                this.result = null;
            } else if (o instanceof Future) { // 如果是结果为Future进入等待逻辑
                this.waiter = Waiter.of(ObjectAide.as(o));
                this.timeout = System.currentTimeMillis() + 5000; // 超时
                this.result = null;
            } else { // 结果处理逻辑
                this.postInvoke();
                this.done = true;
                this.handleResult(cause);
            }
        }

        /* 检测是否有waitable对象*/
        private void checkWaiter() throws Throwable {
            if (this.waiter != null) { // 检测是否
                if (this.waiter.isDone()) {
                    if (this.waiter.isSuccess()) {
                        this.setResult(this.waiter.getResult());
                        this.checkResult(null);
                    } else {
                        this.setResult(this.waiter.getResult());
                        throw this.waiter.getCause();
                    }
                } else {
                    if (System.currentTimeMillis() > timeout) {
                        this.result = null;
                        throw new DispatchTimeoutException(NetResultCode.EXECUTE_TIMEOUT,
                                format("执行 {} 超时", this.controller.getName()));
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void handleResult(Throwable cause) {
            if (!this.isDone())
                return;
            MessageContext<Object> content = null;
            MessageHeader header = message.getHeader();
            ResultCode code;
            Object value = null;
            if (this.result instanceof CommandResult) {
                CommandResult commandResult = (CommandResult) this.result;
                code = commandResult.getResultCode();
                value = commandResult.getBody();
            } else if (this.result instanceof ResultCode) {
                code = (ResultCode) this.result;
            } else {
                code = ResultCode.SUCCESS;
                value = this.result;
            }
            if (cause != null)
                DISPATCHER_LOG.error("Controller [{}] 处理消息异常 {} - {} ", getName(), code, code.getCode(), cause);
            switch (message.getMode()) {
                case PUSH:
                    if (value != null)
                        content = MessageContext.toPush(header, code, value);
                    break;
                case REQUEST:
                    content = MessageContext.toResponse(header, code, value, header.getId());
                    break;
            }
            if (content != null) {
                // NetSession session = this.tunnel.getSession();
                if (code.getType() != ResultCodeType.ERROR) {
                    this.tunnel.send(content);
                    // session.send(this.tunnel, content);
                } else {
                    content.willSendFuture()
                            .whenComplete((tunnel, e) -> this.tunnel.close());
                    this.tunnel.send(content);
                    // session.send(this.tunnel, content);
                }
            }
        }


        @SuppressWarnings("unchecked")
        private void handleException(Throwable e) {
            if (e instanceof DispatchException) {
                DispatchException dex = (DispatchException) e;
                DISPATCHER_LOG.error(dex.getMessage(), dex);
                fireInvokeException(this, dex);
                this.setResult(dex.getResultCode());
                fireInvokeFinish(this, e);
            } else if (e instanceof InvocationTargetException) {
                this.handleException(((InvocationTargetException) e).getTargetException());
            } else if (e instanceof ExecutionException) {
                this.handleException(e.getCause());
            } else {
                DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
                fireInvokeException(this, e);
                this.setResult(NetResultCode.EXECUTE_EXCEPTION);
                fireInvokeFinish(this, e);
            }
        }

        @SuppressWarnings("unchecked")
        private void invoke() throws Exception {
            if (message.getMode() == MessageMode.RESPONSE)
                event.completeResponse();
            //检测认证
            this.checkAuth();

            fireWillInvoke(this);
            if (this.isIntercept())
                return;

            if (this.controller == null) {
                MessageHeader header = message.getHeader();
                DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", header.getProtocol());
                this.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
                return;
            }

            DISPATCHER_LOG.debug("Controller [{}] 开始处理Message : {}\n ", getName(), message);

            // 检测是否controller运行条件
            this.checkController();

            // 调用方法
            DISPATCHER_LOG.debug("Controller [{}] 触Message处理事件", getName());
            fireInvoke(this);
            if (this.isIntercept())
                return;

            DISPATCHER_LOG.debug("Controller [{}] 执行BeforePlugins", getName());
            controller.beforeInvoke(this.tunnel, this.message, this);

            if (this.isIntercept()) {
                return;
            }

            DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
            controller.invoke(this.tunnel, this.message, this);

            fireInvoked(this);
            DISPATCHER_LOG.debug("Controller [{}] 执行业务成功!", getName());
        }

        @SuppressWarnings("unchecked")
        private void postInvoke() {
            if (this.controller != null) {
                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 执行AftertPlugins", getName());
                controller.afterInvoke(this.tunnel, this.message, this);
            }

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
                AuthenticateProvider<Object> provider = null;
                if (this.controller != null) {
                    Class<?> clazz = controller.getAuthProvider();
                    if (clazz != null) {
                        provider = authProviders.get(clazz);
                        Throws.checkNotNull(provider, "{} 认证器不存在", clazz);
                    }
                }
                if (provider == null) {
                    MessageHeader header = message.getHeader();
                    provider = authProviders.getOrDefault(header.getProtocol(), authAllProvider);
                }
                if (provider == null)
                    return;
                if (DISPATCHER_LOG.isDebugEnabled())
                    DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
                Certificate<Object> certificate = provider.validate(this.tunnel, this.message);
                // 是否需要做登录校验,判断是否已经登录
                if (certificate != null && certificate.isAutherized()) {
                    this.tunnel.authenticate(certificate);
                    if (this.message instanceof NetMessage)
                        ((NetMessage<Object>) this.message).update(certificate);
                    SessionKeeperFactory sessionKeeperFactory = appConfiguration.getSessionKeeperFactory();
                    NetSessionKeeper<Object> keeper = sessionKeeperFactory.getKeeper(certificate.getUserType());
                    keeper.online(tunnel);
                    // this.tunnel.getSession().login(loginInfo);
                    // NetSessionHolder sessionHolder = appConfiguration.getSessionHolder();
                    // sessionHolder.online(this.tunnel, loginInfo);
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void checkController() throws DispatchException {
            String appType = appConfiguration.getAppType();
            if (!controller.isActiveByAppType(appType)) {
                DISPATCHER_LOG.error("Controller [{}] 应用类型 {} 无法此协议", getName(), appType);
                throw new DispatchException(NetResultCode.NO_SUCH_PROTOCOL);
            }
            String scopeType = appConfiguration.getScopeType();
            if (!controller.isActiveByScope(scopeType)) {
                DISPATCHER_LOG.error("Controller [{}] 应用类型 {} 无法此协议", getName(), appType);
                throw new DispatchException(NetResultCode.NO_SUCH_PROTOCOL);
            }
            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
            if (controller.isAuth() && !this.tunnel.isLogin()) {
                DISPATCHER_LOG.error("Controller [{}] 用户未登陆", getName());
            }

            if (DISPATCHER_LOG.isDebugEnabled())
                DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", getName());
            if (!controller.isUserGroup(this.tunnel.getUserType())) {
                DISPATCHER_LOG.error("Controller [{}] 用户为[{}]用户组, 无法调用此协议", getName(), this.tunnel.getUserType());
                throw new DispatchException(NetResultCode.NO_PERMISSIONS);
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

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("message", message)
                    .toString();
        }
    }

}

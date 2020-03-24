package com.tny.game.net.command.dispatcher;

import com.tny.game.common.collection.*;
import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;
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
public abstract class AbstractMessageDispatcher implements MessageDispatcher, MessageDispatcherContext {

    /**
     * 所有协议身法验证器
     */
    private AuthenticateValidator defaultValidator;

    /**
     * 所有协议身法验证器
     */
    private EndpointKeeperManager endpointKeeperManager;

    /**
     * 插件管理器
     */
    private Map<Class<?>, CommandPlugin> pluginMap = new CopyOnWriteMap<>();

    /**
     * 派发错误监听器
     */
    private final List<DispatchCommandListener> dispatchListeners = new CopyOnWriteArrayList<>();

    /**
     * 认证器列表
     */
    private final Map<Object, AuthenticateValidator<?>> authValidators = new CopyOnWriteMap<>();

    /**
     * Controller Map
     */
    private Map<Object, Map<MessageMode, MethodControllerHolder>> methodHolder = new ConcurrentHashMap<>();

    private ExprHolderFactory exprHolderFactory = new GroovyExprHolderFactory();

    private AppContext appContext;

    public AbstractMessageDispatcher(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public AppContext getAppContext() {
        return this.appContext;
    }

    @Override
    public Command dispatch(NetTunnel<?> tunnel, Message<?> message) throws CommandException {
        // 获取方法持有器
        MethodControllerHolder controller = this.getController(message.getProtocol(), message.getMode());
        if (controller != null)
            return new ControllerMessageCommand(this, controller, tunnel, message);
        if (message.getMode() == MessageMode.REQUEST)
            throw new CommandException(NetResultCode.NO_SUCH_PROTOCOL,
                    format("{} controller [{}] not exist", message.getMode(), message.getProtocol()));
        return null;
    }

    @Override
    public MethodControllerHolder getController(Object protocol, MessageMode mode) {
        // 获取方法持有器
        MethodControllerHolder controller = null;
        final Map<MessageMode, MethodControllerHolder> controllerMap = this.methodHolder.get(protocol);
        if (controllerMap != null)
            controller = controllerMap.get(mode);
        return controller;
    }

    @Override
    public CommandPlugin getPlugin(Class<? extends CommandPlugin> pluginClass) {
        return this.pluginMap.get(pluginClass);
    }

    @Override
    public AuthenticateValidator getValidator(Object protocol, Class<? extends AuthenticateValidator> validatorClass) {
        AuthenticateValidator<Object> validator = null;
        if (validatorClass != null) {
            validator = as(this.authValidators.get(validatorClass));
            ThrowAide.checkNotNull(validator, "{} 认证器不存在", validatorClass);
        }
        if (validator == null) {
            validator = as(this.authValidators.getOrDefault(protocol, this.defaultValidator));
        }
        return validator;
    }

    @Override
    public List<DispatchCommandListener> getDispatchListeners() {
        return Collections.unmodifiableList(this.dispatchListeners);
    }

    @Override
    public EndpointKeeperManager getEndpointKeeperManager() {
        return this.endpointKeeperManager;
    }

    protected AbstractMessageDispatcher setEndpointKeeperManager(EndpointKeeperManager endpointKeeperManager) {
        this.endpointKeeperManager = endpointKeeperManager;
        return this;
    }

    @Override
    public void addListener(final DispatchCommandListener listener) {
        this.dispatchListeners.add(listener);
    }

    @Override
    public void addListener(final Collection<DispatchCommandListener> listeners) {
        listeners.forEach(this::addListener);
    }

    @Override
    public void removeListener(final DispatchCommandListener listener) {
        this.dispatchListeners.remove(listener);
    }

    @Override
    public void clearListener() {
        this.dispatchListeners.clear();
    }

    protected AbstractMessageDispatcher setAppContext(AppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    /**
     * 添加插件列表
     *
     * @param plugins 插件列表
     */
    protected void addControllerPlugin(Collection<CommandPlugin> plugins) {
        this.pluginMap.putAll(plugins.stream()
                                     .collect(CollectorsAide.toMap(CommandPlugin::getClass)));
    }

    /**
     * 添加插件
     *
     * @param plugin 插件
     */
    protected void addControllerPlugin(CommandPlugin plugin) {
        this.pluginMap.put(plugin.getClass(), plugin);
    }

    /**
     * 添加身份校验器
     *
     * @param provider 身份校验器
     */
    protected void addAuthProvider(AuthenticateValidator provider) {
        Class<?> providerClass = provider.getClass();
        AuthProtocol protocol = providerClass.getAnnotation(AuthProtocol.class);
        if (protocol != null) {
            if (protocol.all()) {
                ThrowAide.checkNotNull(this.defaultValidator, "添加 {} 失败! 存在全局AuthProvider {}", providerClass, this.defaultValidator.getClass());
                this.defaultValidator = provider;
            } else {
                for (int value : protocol.protocol()) {
                    putObject(this.authValidators, value, provider);
                }
            }
        }
        putObject(this.authValidators, provider.getClass(), provider);
    }

    /**
     * 添加身份校验器列表
     *
     * @param providers 身份校验器列表
     */
    protected void addAuthProvider(Collection<AuthenticateValidator> providers) {
        providers.forEach(this::addAuthProvider);
    }

    /**
     * 添加控制器对象列表
     *
     * @param objects 控制器对象列表
     */
    protected void addController(Collection<Object> objects) {
        objects.forEach(this::addController);
    }

    /**
     * 添加控制器对象
     *
     * @param object 控制器对象
     */
    protected void addController(Object object) {
        Map<Object, Map<MessageMode, MethodControllerHolder>> methodHolder = this.methodHolder;
        final ClassControllerHolder holder = new ClassControllerHolder(object, this, this.exprHolderFactory);
        for (Entry<Integer, MethodControllerHolder> entry : holder.getMethodHolderMap().entrySet()) {
            MethodControllerHolder controller = entry.getValue();
            Map<MessageMode, MethodControllerHolder> holderMap = methodHolder.computeIfAbsent(controller.getId(), k -> new CopyOnWriteMap<>());
            for (MessageMode mode : controller.getMessageModes()) {
                MethodControllerHolder old = holderMap.putIfAbsent(mode, controller);
                if (old != null)
                    throw new IllegalArgumentException(format("{} 与 {} 对MessageMode {} 处理发生冲突", old, controller, mode));
            }
        }
    }

    private <K, V> void putObject(Map<K, V> map, K key, V value) {
        V oldValue = map.put(key, value);
        if (oldValue != null)
            ThrowAide.throwException(IllegalArgumentException::new,
                    "添加 {} 失败! key {} 存在 {} 对象", value.getClass(), key, oldValue.getClass());
    }

}

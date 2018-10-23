package com.tny.game.net.command.dispatcher;

import com.tny.game.common.collection.*;
import com.tny.game.common.utils.Throws;
import com.tny.game.common.worker.command.Command;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.annotation.AuthProtocol;
import com.tny.game.net.base.*;
import com.tny.game.net.command.plugins.ControllerPlugin;
import com.tny.game.net.command.auth.AuthenticateValidator;
import com.tny.game.net.command.listener.DispatchCommandListener;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.NetTunnel;

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
public abstract class AbstractMessageDispatcher implements MessageDispatcher {

    /**
     * Controller Map
     */
    private Map<Object, Map<MessageMode, MethodControllerHolder>> methodHolder = new ConcurrentHashMap<>();

    /**
     * 所有协议身法验证器
     */
    private AuthenticateValidator fullValidator;

    /**
     *
     */
    private Map<Class<?>, ControllerPlugin> pluginMap = new CopyOnWriteMap<>();

    /**
     * 派发错误监听器
     */
    private final List<DispatchCommandListener> dispatchListeners = new CopyOnWriteArrayList<>();

    /**
     * 认证器列表
     */
    private final Map<Object, AuthenticateValidator<?>> authValidators = new CopyOnWriteMap<>();

    private AppConfiguration appConfiguration;

    public AbstractMessageDispatcher(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public Command dispatch(NetTunnel<?> tunnel, Message<?> message) throws CommandException {
        // 获取方法持有器
        MethodControllerHolder controller = this.getController(message.getProtocol(), message.getMode());
        if (controller == null)
            throw new CommandException(NetResultCode.NO_SUCH_PROTOCOL);
        return new MessageDispatchCommand(this, controller, tunnel, message);
    }

    @Override
    public AppConfiguration getAppConfiguration() {
        return appConfiguration;
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
    public ControllerPlugin getPlugin(Class<? extends ControllerPlugin> pluginClass) {
        return this.pluginMap.get(pluginClass);
    }

    @Override
    public AuthenticateValidator getValidator(Object protocol, Class<? extends AuthenticateValidator> validatorClass) {
        AuthenticateValidator<Object> validator = null;
        if (validatorClass != null) {
            validator = as(authValidators.get(validatorClass));
            Throws.checkNotNull(validator, "{} 认证器不存在", validatorClass);
        }
        if (validator == null) {
            validator = as(authValidators.getOrDefault(protocol, fullValidator));
        }
        return validator;
    }

    @Override
    public List<DispatchCommandListener> getDispatchListeners() {
        return Collections.unmodifiableList(dispatchListeners);
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

    /**
     * 添加插件列表
     *
     * @param plugins 插件列表
     */
    protected void addControllerPlugin(Collection<ControllerPlugin> plugins) {
        this.pluginMap.putAll(plugins.stream()
                .collect(CollectorsAide.toMap(ControllerPlugin::getClass)));
    }

    /**
     * 添加插件
     *
     * @param plugin 插件
     */
    protected void addControllerPlugin(ControllerPlugin plugin) {
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
                Throws.checkNotNull(this.fullValidator, "添加 {} 失败! 存在全局AuthProvider {}", providerClass, fullValidator.getClass());
                this.fullValidator = provider;
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
        ExprHolderFactory exprHolderFactory = appConfiguration.getExprHolderFactory();
        Map<Object, Map<MessageMode, MethodControllerHolder>> methodHolder = this.methodHolder;
        final ClassControllerHolder holder = new ClassControllerHolder(object, this, exprHolderFactory);
        for (Entry<Integer, MethodControllerHolder> entry : holder.getMethodHolderMap().entrySet()) {
            MethodControllerHolder controller = entry.getValue();
            Map<MessageMode, MethodControllerHolder> holderMap = methodHolder.computeIfAbsent(controller.getID(), k -> new CopyOnWriteMap<>());
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
            Throws.throwException(IllegalArgumentException::new,
                    "添加 {} 失败! key {} 存在 {} 对象", value.getClass(), key, oldValue.getClass());
    }

}

package com.tny.game.net.command.dispatcher;

import com.tny.game.common.collection.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/6 2:29 下午
 */
public class DefaultMessageDispatcherContext implements MessageDispatcherContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageDispatcherContext.class);

    private NetAppContext appContext;

    /**
     * 所有协议身法验证器
     */
    private AuthenticateValidator<?> defaultValidator;

    /**
     * 插件管理器
     */
    private final Map<Class<?>, CommandPlugin<?, ?>> pluginMap = new CopyOnWriteMap<>();

    /**
     * 认证器列表
     */
    private final Map<Object, AuthenticateValidator<?>> authValidators = new CopyOnWriteMap<>();

    /**
     * 派发错误监听器
     */
    private final List<MessageCommandListener> commandListeners = new CopyOnWriteArrayList<>();

    public DefaultMessageDispatcherContext(NetAppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public NetAppContext getAppContext() {
        return this.appContext;
    }

    @Override
    public CommandPlugin<?, ?> getPlugin(Class<? extends CommandPlugin<?, ?>> pluginClass) {
        return this.pluginMap.get(pluginClass);
    }

    @Override
    public AuthenticateValidator<?> getValidator(Object protocol, Class<? extends AuthenticateValidator<?>> validatorClass) {
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
    public Collection<MessageCommandListener> getCommandListener() {
        return Collections.unmodifiableCollection(this.commandListeners);
    }

    /**
     * 添加插件列表
     *
     * @param plugins 插件列表
     */
    protected void addControllerPlugin(Collection<? extends CommandPlugin<?, ?>> plugins) {
        this.pluginMap.putAll(plugins.stream()
                .collect(CollectorsAide.toMap(CommandPlugin::getClass)));
    }

    /**
     * 添加插件
     *
     * @param plugin 插件
     */
    protected void addControllerPlugin(CommandPlugin<?, ?> plugin) {
        this.pluginMap.put(plugin.getClass(), plugin);
    }

    /**
     * 添加身份校验器
     *
     * @param provider 身份校验器
     */
    protected void addAuthProvider(AuthenticateValidator<?> provider) {
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
    protected void addAuthProvider(Collection<? extends AuthenticateValidator<?>> providers) {
        providers.forEach(this::addAuthProvider);
    }

    @Override
    public void addCommandListener(final MessageCommandListener listener) {
        this.commandListeners.add(listener);
    }

    @Override
    public void addCommandListener(final Collection<MessageCommandListener> listeners) {
        listeners.forEach(this::addCommandListener);
    }

    @Override
    public void removeCommandListener(final MessageCommandListener listener) {
        this.commandListeners.remove(listener);
    }

    @Override
    public void clearCommandListeners() {
        this.commandListeners.clear();
    }

    private <K, V> void putObject(Map<K, V> map, K key, V value) {
        V oldValue = map.put(key, value);
        if (oldValue != null) {
            ThrowAide.throwException(IllegalArgumentException::new,
                    "添加 {} 失败! key {} 存在 {} 对象", value.getClass(), key, oldValue.getClass());
        }
    }

}

package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.ServerConfigFactory;
import com.tny.game.net.dispatcher.plugin.PluginHolder;

public class ServerConfiguration {

    /**
     * 插件管理器
     */
    private PluginHolder pluginHolder;

    /**
     * 请求校验器
     */
    private RequestChecker checker;

    /**
     * 會話持有對象
     */
    private NetSessionHolder sessionHolder;

    /**
     * 会话验证器
     */
    private AuthProvider authProvider;

    /**
     * 服务器上下文
     */
    private ServerConfig serverContext;

    /**
     * 请求分发器
     */
    private MessageDispatcher controllerDispatcher;

    private ServerConfiguration() {
    }

    public PluginHolder getPluginHolder() {
        return pluginHolder;
    }

    public void setPluginHolder(PluginHolder pluginHolder) {
        if (pluginHolder != null)
            throw new IllegalArgumentException(LogUtils.format("{} 与 {} pluginHolder 冲突", this.pluginHolder.getClass(), pluginHolder.getClass()));
        this.pluginHolder = pluginHolder;
    }

    public RequestChecker getChecker() {
        return checker;
    }

    public void setChecker(RequestChecker checker) {
        if (checker != null)
            throw new IllegalArgumentException(LogUtils.format("{} 与 {} checker 冲突", this.checker.getClass(), checker.getClass()));
        this.checker = checker;
    }

    public NetSessionHolder getSessionHolder() {
        return sessionHolder;
    }

    public void setSessionHolder(NetSessionHolder sessionHolder) {
        if (sessionHolder != null)
            throw new IllegalArgumentException(LogUtils.format("{} 与 {} sessionHolder 冲突", this.sessionHolder.getClass(), sessionHolder.getClass()));
        this.sessionHolder = sessionHolder;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        if (authProvider != null)
            throw new IllegalArgumentException(LogUtils.format("{} 与 {} authProvider 冲突", this.authProvider.getClass(), authProvider.getClass()));
        this.authProvider = authProvider;
    }

    public ServerConfig getServerContext() {
        return serverContext;
    }

    public void setServerContextFactory(ServerConfigFactory factory) {
        if (serverContext != null)
            throw new IllegalArgumentException(LogUtils.format("{} 与 {} serverContext 冲突", serverContext.getClass(), factory.getServerContext().getClass()));
        this.serverContext = factory.getServerContext();
    }

    public MessageDispatcher getControllerDispatcher() {
        return controllerDispatcher;
    }

    public void setControllerDispatcher(MessageDispatcher controllerDispatcher) {
        if (controllerDispatcher != null)
            throw new IllegalArgumentException(LogUtils.format("{} 与 {} controllerDispatcher 冲突", this.controllerDispatcher.getClass(), controllerDispatcher.getClass()));
        this.controllerDispatcher = controllerDispatcher;
    }

}

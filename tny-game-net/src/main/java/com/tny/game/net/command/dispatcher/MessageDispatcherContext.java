package com.tny.game.net.command.dispatcher;

import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.auth.AuthenticateProvider;
import com.tny.game.net.command.listener.DispatchCommandListener;
import com.tny.game.net.transport.message.MessageMode;

import java.util.List;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-30 18:03
 */
public interface MessageDispatcherContext {

    /**
     * @return 获取应用配置
     */
    AppConfiguration<?> getAppConfiguration();

    /**
     * 获取 ControllerHolder
     *
     * @param object 协议 ID
     * @return 返回 ControllerHolder
     */
    MethodControllerHolder getController(Object object, MessageMode mode);

    /**
     * 获取插件
     *
     * @param pluginClass 抄件类型
     * @return 返回 ControllerHolder
     */
    ControllerPlugin getPlugin(Class<? extends ControllerPlugin> pluginClass);

    /**
     * 获取身份校验器
     *
     * @param protocol 协议
     * @return 返回身份校验器
     */
    AuthenticateProvider getProvider(Object protocol, Class<? extends AuthenticateProvider> providerClass);

    /**
     * @return 获取所有监听器
     */
    List<DispatchCommandListener> getDispatchListeners();

}

package com.tny.game.net.command.dispatcher;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

import java.util.List;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-30 18:03
 */
@UnitInterface
public interface MessageDispatcherContext {

    /**
     * @return 获取应用配置
     */
    AppContext getAppContext();

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
    CommandPlugin getPlugin(Class<? extends CommandPlugin> pluginClass);

    /**
     * 获取身份校验器
     *
     * @param protocol 协议
     * @return 返回身份校验器
     */
    AuthenticateValidator getValidator(Object protocol, Class<? extends AuthenticateValidator> validatorClass);

    /**
     * @return 获取所有监听器
     */
    List<DispatchCommandListener> getDispatchListeners();

    /**
     * @return 获取 endpoint
     */
    EndpointKeeperManager getEndpointKeeperManager();

}

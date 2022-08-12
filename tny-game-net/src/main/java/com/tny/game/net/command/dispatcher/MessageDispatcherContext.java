/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command.dispatcher;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;

import java.util.Collection;

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
    NetAppContext getAppContext();

    /**
     * 获取插件
     *
     * @param pluginClass 抄件类型
     * @return 返回 ControllerHolder
     */
    CommandPlugin<?, ?> getPlugin(Class<? extends CommandPlugin<?, ?>> pluginClass);

    /**
     * 获取身份校验器
     *
     * @param protocol 协议
     * @return 返回身份校验器
     */
    AuthenticateValidator<?, ?> getValidator(Object protocol);

    /**
     * 获取身份校验器
     *
     * @param validatorClass 类
     * @return 返回身份校验器
     */
    AuthenticateValidator<?, ?> getValidator(Class<? extends AuthenticateValidator<?, ?>> validatorClass);

    Collection<MessageCommandListener> getCommandListener();

    void addCommandListener(MessageCommandListener listener);

    void addCommandListener(Collection<MessageCommandListener> listeners);

    void removeCommandListener(MessageCommandListener listener);

    void clearCommandListeners();

}

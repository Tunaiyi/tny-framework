/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-09-30 18:03
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
    CommandPlugin<?> getPlugin(Class<? extends CommandPlugin<?>> pluginClass);

    /**
     * 获取身份校验器
     *
     * @param protocol 协议
     * @return 返回身份校验器
     */
    AuthenticationValidator getValidator(Object protocol);

    /**
     * 获取身份校验器
     *
     * @param validatorClass 类
     * @return 返回身份校验器
     */
    AuthenticationValidator getValidator(Class<? extends AuthenticationValidator> validatorClass);

    /**
     * 鉴权校验器单点解析：方法级 → 协议级 → 全局兜底（command-execution"鉴权校验器可注册且按维度生效"）。
     *
     * @param methodLevelValidator 方法/类显式声明的校验器类，无声明传 null
     * @param protocol             协议号
     * @return 解析到的校验器，三级皆无返回 null（调用方按未登录拒绝）
     */
    default AuthenticationValidator resolveValidator(Class<? extends AuthenticationValidator> methodLevelValidator, Object protocol) {
        if (methodLevelValidator != null) {
            return getValidator(methodLevelValidator);
        }
        // 协议级查找自带全局兜底
        return getValidator(protocol);
    }

    Collection<MessageCommandListener> getCommandListener();

    void addCommandListener(MessageCommandListener listener);

    void addCommandListener(Collection<MessageCommandListener> listeners);

    void removeCommandListener(MessageCommandListener listener);

    void clearCommandListeners();

}

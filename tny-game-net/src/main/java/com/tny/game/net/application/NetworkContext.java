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
package com.tny.game.net.application;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

/**
 * 网络上下文对象
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 2:22 下午
 */
public interface NetworkContext extends SessionContext {

    /**
     * @return 应用上下文
     */
    NetAppContext getAppContext();

    /**
     * @return 网络启动器配置
     */
    NetBootstrapSetting getSetting();

    /**
     * @return 接入模式
     */
    @Override
    default NetAccessMode getAccessMode() {
        return getSetting().getAccessMode();
    }

    /**
     * @return 消息工厂
     */
    MessageFactory getMessageFactory();

    SessionFactory getSessionFactory();

    /**
     * @return 消息者工厂
     */
    ContactFactory getContactFactory();

    /**
     * @return Rpc转发器
     */
    RpcForwarder getRpcForwarder();

    /**
     * @return Rpc 监控器
     */
    RpcMonitor getRpcMonitor();

}

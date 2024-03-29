/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.base;

import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;

/**
 * 网络上下文对象
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 2:22 下午
 */
public interface NetworkContext extends EndpointContext {

    /**
     * @return 应用上下文
     */
    NetAppContext getAppContext();

    /**
     * @return 网络启动器配置
     */
    NetBootstrapSetting getSetting();

    /**
     * @return 消息工厂
     */
    MessageFactory getMessageFactory();

    /**
     * @return 消息者工厂
     */
    MessagerFactory getMessagerFactory();

    /**
     * @param <UID> id类型
     * @return 选项工厂
     */
    <UID> CertificateFactory<UID> getCertificateFactory();

    /**
     * @return Rpc转发器
     */
    RpcForwarder getRpcForwarder();

}

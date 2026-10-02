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

import com.tny.game.net.application.configuration.*;
import com.tny.game.net.rpc.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public interface NetBootstrapSetting extends ServiceSetting {

    /**
     * @return 服务名
     */
    String getName();

    /**
     * @return 是否可转发
     */
    boolean isForwardable();

    /**
     * @return 接入模式
     */
    NetAccessMode getAccessMode();

    String getTunnelIdGenerator();

    String getMessageFactory();

    String getSessionFactory();

    String getContactFactory();

    String getMessageDispatcher();

    String getCommandExecutorFactory();

    String getRpcForwarder();

    Set<String> getReadIgnoreHeaders();

    Set<String> getWriteIgnoreHeaders();

    default RpcServiceType getRpcServiceType() {
        String name = serviceName();
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return RpcServiceTypes.ofService(name);
    }

}

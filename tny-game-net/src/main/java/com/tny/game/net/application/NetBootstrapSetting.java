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

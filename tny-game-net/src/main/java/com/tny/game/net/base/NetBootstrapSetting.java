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

import org.apache.commons.lang3.StringUtils;

public interface NetBootstrapSetting {

    /**
     * @return 服务名
     */
    String getName();

    /**
     * @return 是否可转发
     */
    boolean isForwardable();

    /**
     * @return 服务发现 服务名
     */
    String getServeName();

    /**
     * @return 服务名
     */
    String serviceName();

    String getTunnelIdGenerator();

    String getMessageFactory();

    String getMessagerFactory();

    String getCertificateFactory();

    String getMessageDispatcher();

    String getCommandTaskProcessor();

    String getRpcForwarder();

    default RpcServiceType getRpcServiceType() {
        String name = serviceName();
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return RpcServiceTypes.ofService(name);
    }

}

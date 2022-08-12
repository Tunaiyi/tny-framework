/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.actor;

import org.apache.commons.lang3.StringUtils;

/**
 * Actor 地址
 *
 * @author KGTny
 */
public class ActorAddress {

    private final String protocol;

    private final String system;

    private final String host;

    private final Integer port;

    public ActorAddress(String protocol, String system, String host, Integer port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.system = system;
    }

    public ActorAddress(String protocol, String system) {
        this(protocol, system, null, null);
    }

    /**
     * @return 返回是否是本地作用域名
     */
    public boolean hasLocalScope() {
        return StringUtils.isBlank(host);
    }

    /**
     * @return 返回是否是全局作用域名
     */
    public boolean hasGlobalScope() {
        return StringUtils.isNotBlank(host);
    }

    /**
     * @return <system>@<host>:<port>
     */
    public String hostPort() {
        StringBuilder sb = (new StringBuilder(system));
        if (StringUtils.isNoneBlank(host)) {
            sb.append('@').append(host);
        }
        if (port != null) {
            sb.append(':').append(port);
        }
        return sb.toString();
    }

    /**
     * @return <protocol>://<system>@<host>:<port>
     */
    @Override
    public String toString() {
        StringBuilder sb = (new StringBuilder(protocol)).append("://").append(system);
        if (StringUtils.isNoneBlank(host)) {
            sb.append('@').append(host);
        }
        if (port != null) {
            sb.append(':').append(port);
        }
        return sb.toString();
    }

}

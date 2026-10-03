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

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

package com.tny.game.common.utils;

import java.net.*;
import java.util.regex.Pattern;

/**
 * Created by Kun Yang on 2017/9/7.
 */
public interface IpAide {

    Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    /**
     * @param hostName 域名
     * @return 获取hostName的IP
     */
    static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    static boolean isLocalHost(String host) {
        return host != null
               && (LOCAL_IP_PATTERN.matcher(host).matches()
                   || host.equalsIgnoreCase("localhost"));
    }

}

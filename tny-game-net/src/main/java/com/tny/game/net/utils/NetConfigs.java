/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.utils;

/*
 * Created by Kun Yang on 2017/3/26.*/
public interface NetConfigs {

    String CONNECT_TIMEOUT_URL_PARAM = "connect_timeout";
    long CONNECT_TIMEOUT_DEFAULT_VALUE = 5000L;

    String CONNECT_ASYNC_URL_PARAM = "connect_async";
    boolean CONNECT_ASYNC_DEFAULT_VALUE = false;

    String AUTO_RECONNECT_PARAM = "auto_reconnect";
    boolean AUTO_RECONNECT_DEFAULT_VALUE = true;

    String RETRY_TIMES_URL_PARAM = "retry";
    int RETRY_TIMES_DEFAULT_VALUE = -1;

    String RETRY_INTERVAL_URL_PARAM = "retry_intervals";
    long RETRY_INTERVAL_DEFAULT_VALUE = 3000L;

}

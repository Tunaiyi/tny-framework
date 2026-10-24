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

package com.tny.game.net.utils;

/*
 * Created by Kun Yang on 2017/3/26.*/
public interface NetConfigs {

    String CONNECT_TIMEOUT_URL_PARAM = "connect_timeout";
    long CONNECT_TIMEOUT_DEFAULT_VALUE = 5000L;

    String CONNECT_ASYNC_URL_PARAM = "connect_async";

    String AUTO_RECONNECT_PARAM = "auto_reconnect";
    boolean AUTO_RECONNECT_DEFAULT_VALUE = true;

    String RETRY_TIMES_URL_PARAM = "retry";
    int RETRY_TIMES_DEFAULT_VALUE = -1;

    String RETRY_INTERVAL_URL_PARAM = "retry_intervals";
    long RETRY_INTERVAL_DEFAULT_VALUE = 3000L;

}

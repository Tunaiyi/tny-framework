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
package com.tny.game.net.application.configuration;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/28 09:37
 **/
public interface ServiceSetting {

    /**
     * @return 获取服务名
     */
    String getService();

    /**
     * @return 发现服务器服务名
     */
    String getServeName();

    /**
     * @return 获取服务名(获取服务名 未设置则返回ServeName)
     */
    default String serviceName() {
        return ifBlank(this.getService(), this.getServeName());
    }

    /**
     * @return 获取服务名(获取服务名 未设置则返回ServeName)
     */
    default String discoverService() {
        return ifBlank(this.getServeName(), this.getService());
    }
}

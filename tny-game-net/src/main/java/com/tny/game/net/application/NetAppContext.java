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

import com.tny.game.common.context.*;
import com.tny.game.common.lifecycle.unit.annotation.*;

import java.util.List;

import static com.tny.game.common.utils.StringAide.*;

@UnitInterface
public interface NetAppContext {

    /**
     * @return 应用名字
     */
    String getName();

    /**
     * @return 获取服务名
     */
    String getService();

    /**
     * @return 应用类型标识
     */
    String getAppType();

    /**
     * @return 应用类型
     */
    default AppType appType() {
        return AppTypes.ofAppName(this.getAppType());
    }

    /**
     * @return 作用域类型标识
     */
    String getScopeType();

    /**
     * @return 作用域类型
     */
    default AppScope scopeType() {
        return AppScopes.ofScopeName(this.getScopeType());
    }

    /**
     * @return 获取服务名(获取服务名 未设置则返回ServeName)
     */
    default String serviceName() {
        return ifBlank(this.getService(), this.getAppType());
    }

    /**
     * @return 本地
     */
    String getLocale();

    /**
     * 全局唯一 id
     * 确保所有的服务器类型的 id 都不重复
     *
     * @return 唯一 id
     */
    int getServerId();

    /**
     * @return 获取加载包路径
     */
    List<String> getScanPackages();

    /**
     * @return 全局上下文
     */
    Attributes attributes();

}

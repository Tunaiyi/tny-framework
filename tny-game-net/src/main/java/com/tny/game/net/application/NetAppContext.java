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

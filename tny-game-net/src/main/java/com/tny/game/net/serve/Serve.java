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
package com.tny.game.net.serve;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/13 1:09 上午
 */
public interface Serve {

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

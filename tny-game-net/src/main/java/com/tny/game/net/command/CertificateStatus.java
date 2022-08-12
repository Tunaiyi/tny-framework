/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command;

import com.tny.game.common.enums.*;

public enum CertificateStatus implements IntEnumerable {

    /**
     * 无效的
     */
    INVALID(0, false),

    /**
     * 未认证
     */
    UNAUTHENTICATED(1, false),

    /**
     * 已认证
     */
    AUTHENTICATED(2, true),

    /**
     * 续约认证
     */
    RENEW(3, true),

    //
    ;

    private final Integer id;

    private final boolean authenticated;

    CertificateStatus(Integer id, boolean authenticated) {
        this.id = id;
        this.authenticated = authenticated;
    }

    @Override
    public int id() {
        return this.id;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }
}
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

package com.tny.game.net.transport;

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
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

package com.tny.game.net.session;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class CommonSessionFactory implements SessionFactory {

    public CommonSessionFactory() {
    }

    @Override
    public CommonSession create(SessionContext sessionContext, NetTunnel tunnel) {
        return new CommonSession(Certificates.anonymous(), sessionContext, tunnel, 0);
    }

}

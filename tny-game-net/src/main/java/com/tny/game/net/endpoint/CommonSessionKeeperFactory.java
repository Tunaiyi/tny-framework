/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

/**
 * <p>
 */
@Unit
public class CommonSessionKeeperFactory implements SessionKeeperFactory<SessionKeeperSetting> {

    public CommonSessionKeeperFactory() {
    }


    @Override
    public NetEndpointKeeper<Session> createKeeper(ContactType contactType, SessionKeeperSetting setting) {
        SessionFactory<NetSession, SessionSetting> sessionFactory = UnitLoader.getLoader(SessionFactory.class)
                                                                              .checkUnit(setting.getSessionFactory());
        return new CommonSessionKeeper(contactType, sessionFactory, setting);
    }
}

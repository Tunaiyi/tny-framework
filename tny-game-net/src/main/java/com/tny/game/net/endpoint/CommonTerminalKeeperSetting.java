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

package com.tny.game.net.endpoint;

import static com.tny.game.net.application.configuration.NetUnitNames.*;

/**
 * <p>
 */
public class CommonTerminalKeeperSetting implements TerminalKeeperSetting {

    private String name;

    private String keeperFactory = defaultName(TerminalKeeperFactory.class);

    public String getName() {
        return name;
    }

    @Override
    public String getContactType() {
        return this.name;
    }

    @Override
    public String getKeeperFactory() {
        return this.keeperFactory;
    }

    public CommonTerminalKeeperSetting setKeeperFactory(String keeperFactory) {
        this.keeperFactory = unitName(keeperFactory, TerminalKeeperFactory.class);
        return this;
    }

    public CommonTerminalKeeperSetting setName(String name) {
        this.name = name;
        return this;
    }

}

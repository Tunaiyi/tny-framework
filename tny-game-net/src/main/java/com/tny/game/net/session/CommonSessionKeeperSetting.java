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

import static com.tny.game.common.lifecycle.unit.UnitNames.*;

/**
 * <p>
 */
public class CommonSessionKeeperSetting implements SessionKeeperSetting {

    private String name;

    private long offlineCloseDelay = 0;

    private int offlineMaxSize = 0;

    private long clearInterval = 60000;

    private CommonSessionSetting session = new CommonSessionSetting();

    private String keeperFactory = defaultName(SessionKeeperFactory.class);

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

    @Override
    public long getOfflineCloseDelay() {
        return this.offlineCloseDelay;
    }

    @Override
    public int getOfflineMaxSize() {
        return this.offlineMaxSize;
    }

    @Override
    public long getClearInterval() {
        return this.clearInterval;
    }

    @Override
    public SessionSetting getSession() {
        return this.session;
    }

    public CommonSessionKeeperSetting setOfflineCloseDelay(long offlineCloseDelay) {
        this.offlineCloseDelay = offlineCloseDelay;
        return this;
    }

    public CommonSessionKeeperSetting setOfflineMaxSize(int offlineMaxSize) {
        this.offlineMaxSize = offlineMaxSize;
        return this;
    }

    public CommonSessionKeeperSetting setClearInterval(long clearInterval) {
        this.clearInterval = clearInterval;
        return this;
    }

    public CommonSessionKeeperSetting setKeeperFactory(String keeperFactory) {
        this.keeperFactory = keeperFactory;
        return this;
    }

    public CommonSessionKeeperSetting setName(String name) {
        this.name = name;
        return this;
    }

    public CommonSessionKeeperSetting setSession(CommonSessionSetting session) {
        this.session = session;
        return this;
    }

}

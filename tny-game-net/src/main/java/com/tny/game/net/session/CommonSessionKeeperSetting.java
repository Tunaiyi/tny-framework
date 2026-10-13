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

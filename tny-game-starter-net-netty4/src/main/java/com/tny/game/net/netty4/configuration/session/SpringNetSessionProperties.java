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

package com.tny.game.net.netty4.configuration.session;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.utils.*;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 3:45 上午
 */
@ConfigurationProperties(prefix = "tny.net.session")
public class SpringNetSessionProperties {

    private Map<String, SpringNetSessionKeeperSetting> sessionKeeperSettings = new HashMap<>();

    @NestedConfigurationProperty
    private SpringNetSessionKeeperSetting sessionKeeper = new SpringNetSessionKeeperSetting();

    public SpringNetSessionKeeperSetting getSessionKeeper() {
        return this.sessionKeeper;
    }

    public SpringNetSessionProperties setSessionKeeper(SpringNetSessionKeeperSetting sessionKeeper) {
        this.sessionKeeper = sessionKeeper;
        return this;
    }

    public Map<String, SpringNetSessionKeeperSetting> getSessionKeeperSettings() {
        return this.sessionKeeperSettings;
    }

    public SpringNetSessionProperties setSessionKeeperSettings(
            Map<String, SpringNetSessionKeeperSetting> sessionKeeperSettings) {
        sessionKeeperSettings.forEach((name, setting) -> setting.setName(StringAide.ifBlank(setting.getContactType(), name)));
        this.sessionKeeperSettings = ImmutableMap.copyOf(sessionKeeperSettings);
        return this;
    }

}

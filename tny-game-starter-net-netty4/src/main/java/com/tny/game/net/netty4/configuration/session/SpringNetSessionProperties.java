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

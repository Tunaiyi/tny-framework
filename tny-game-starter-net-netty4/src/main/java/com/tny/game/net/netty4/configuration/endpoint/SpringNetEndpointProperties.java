/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.configuration.endpoint;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.utils.*;
import org.springframework.boot.context.properties.*;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 3:45 上午
 */
@ConfigurationProperties(prefix = "tny.net.endpoint")
public class SpringNetEndpointProperties {

    private Map<String, SpringNetSessionKeeperSetting> sessionKeeperSettings = ImmutableMap.of();

    @NestedConfigurationProperty
    private SpringNetSessionKeeperSetting sessionKeeper = new SpringNetSessionKeeperSetting();

    @NestedConfigurationProperty
    private SpringNetTerminalKeeperSetting terminalKeeper = new SpringNetTerminalKeeperSetting();

    private Map<String, SpringNetTerminalKeeperSetting> terminalKeeperSettings = ImmutableMap.of();

    public SpringNetSessionKeeperSetting getSessionKeeper() {
        return this.sessionKeeper;
    }

    public SpringNetEndpointProperties setSessionKeeper(SpringNetSessionKeeperSetting sessionKeeper) {
        this.sessionKeeper = sessionKeeper;
        return this;
    }

    public Map<String, SpringNetSessionKeeperSetting> getSessionKeeperSettings() {
        return this.sessionKeeperSettings;
    }

    public SpringNetEndpointProperties setSessionKeeperSettings(
            Map<String, SpringNetSessionKeeperSetting> sessionKeeperSettings) {
        sessionKeeperSettings.forEach((name, setting) -> setting.setName(StringAide.ifBlank(setting.getMessagerType(), name)));
        this.sessionKeeperSettings = ImmutableMap.copyOf(sessionKeeperSettings);
        return this;
    }

    public SpringNetTerminalKeeperSetting getTerminalKeeper() {
        return this.terminalKeeper;
    }

    public SpringNetEndpointProperties setTerminalKeeper(
            SpringNetTerminalKeeperSetting terminalKeeper) {
        this.terminalKeeper = terminalKeeper;
        return this;
    }

    public Map<String, SpringNetTerminalKeeperSetting> getTerminalKeeperSettings() {
        return this.terminalKeeperSettings;
    }

    public SpringNetEndpointProperties setTerminalKeeperSettings(Map<String, SpringNetTerminalKeeperSetting> terminalKeeperSettings) {
        terminalKeeperSettings.forEach((name, setting) -> setting.setName(StringAide.ifBlank(setting.getMessagerType(), name)));
        this.terminalKeeperSettings = ImmutableMap.copyOf(terminalKeeperSettings);
        return this;
    }

}

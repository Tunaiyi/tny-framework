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

package com.tny.game.basics.configuration;

import com.tny.game.common.scheduler.*;
import org.springframework.boot.context.properties.*;

import java.util.*;

import static com.tny.game.basics.configuration.BasicsPropertyConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:23 下午
 */
@ConfigurationProperties(BASICS_TIME_TASK)
public class BasicsTimeTaskProperties implements TimeTaskSchemesSetting {

    private boolean enable = false;

    private int id;

    private int maxTaskSize = 3000;

    private List<DefaultTimeTaskScheme> schemes = new ArrayList<>();

    @NestedConfigurationProperty
    private TimeTaskPluginSetting plugin = new TimeTaskPluginSetting();

    public boolean isEnable() {
        return enable;
    }

    public BasicsTimeTaskProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public int getId() {
        return id;
    }

    public BasicsTimeTaskProperties setId(int id) {
        this.id = id;
        return this;
    }

    public List<DefaultTimeTaskScheme> getSchemes() {
        return schemes;
    }

    public BasicsTimeTaskProperties setSchemes(List<DefaultTimeTaskScheme> schemes) {
        this.schemes = schemes;
        return this;
    }

    public TimeTaskPluginSetting getPlugin() {
        return plugin;
    }

    public BasicsTimeTaskProperties setPlugin(TimeTaskPluginSetting plugin) {
        this.plugin = plugin;
        return this;
    }

    public int getMaxTaskSize() {
        return maxTaskSize;
    }

    public BasicsTimeTaskProperties setMaxTaskSize(int maxTaskSize) {
        this.maxTaskSize = maxTaskSize;
        return this;
    }

    @Override
    public List<TimeTaskScheme> getTimeTaskSchemeList() {
        return new ArrayList<>(schemes);
    }

}

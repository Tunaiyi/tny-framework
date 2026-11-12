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

package com.tny.game.data.configuration.cache;

import com.tny.game.data.cache.*;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 11:27 上午
 */
@ConfigurationProperties(prefix = "tny.data.object-cache.release.timeout-strategy")
public class ReleaseStrategyProperties {

    @NestedConfigurationProperty
    private TimeoutReleaseStrategySetting strategy = new TimeoutReleaseStrategySetting();

    private Map<String, TimeoutReleaseStrategySetting> strategies = new HashMap<>();

    public TimeoutReleaseStrategySetting getStrategy() {
        return strategy;
    }

    public ReleaseStrategyProperties setStrategy(TimeoutReleaseStrategySetting strategy) {
        this.strategy = strategy;
        return this;
    }

    public Map<String, TimeoutReleaseStrategySetting> getStrategies() {
        return strategies;
    }

    public ReleaseStrategyProperties setStrategies(Map<String, TimeoutReleaseStrategySetting> strategies) {
        this.strategies = strategies;
        return this;
    }

}

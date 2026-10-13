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

import com.tny.game.boot.utils.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:21 下午
 */
public class LocalObjectCacheFactorySetting {

    private String releaseStrategyFactory = BeanNameUtils.lowerCamelName(TimeoutReleaseStrategyFactory.class);

    private String recycler = BeanNameUtils.lowerCamelName(ScheduledCacheRecycler.class);

    public String getRecycler() {
        return recycler;
    }

    public LocalObjectCacheFactorySetting setRecycler(String recycler) {
        this.recycler = recycler;
        return this;
    }

    public String getReleaseStrategyFactory() {
        return releaseStrategyFactory;
    }

    public LocalObjectCacheFactorySetting setReleaseStrategyFactory(String releaseStrategyFactory) {
        this.releaseStrategyFactory = releaseStrategyFactory;
        return this;
    }

}

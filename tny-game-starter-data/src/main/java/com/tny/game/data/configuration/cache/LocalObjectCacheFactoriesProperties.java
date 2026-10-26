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

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:21 下午
 */
@ConfigurationProperties(prefix = "tny.data.object-cache.local-cache")
public class LocalObjectCacheFactoriesProperties {

    private boolean enable = true;

    @NestedConfigurationProperty
    private LocalObjectCacheFactorySetting cache = new LocalObjectCacheFactorySetting();

    private Map<String, LocalObjectCacheFactorySetting> caches = new HashMap<>();

    public boolean isEnable() {
        return enable;
    }

    public LocalObjectCacheFactoriesProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public LocalObjectCacheFactorySetting getCache() {
        return cache;
    }

    public LocalObjectCacheFactoriesProperties setCache(LocalObjectCacheFactorySetting cache) {
        this.cache = cache;
        return this;
    }

    public LocalObjectCacheFactorySetting getCache(String name) {
        if (name.isEmpty()) {
            return this.cache;
        }
        return caches.get(name);
    }

    public Map<String, LocalObjectCacheFactorySetting> getCaches() {
        return caches;
    }

    public LocalObjectCacheFactoriesProperties setCaches(Map<String, LocalObjectCacheFactorySetting> caches) {
        this.caches = caches;
        return this;
    }

}

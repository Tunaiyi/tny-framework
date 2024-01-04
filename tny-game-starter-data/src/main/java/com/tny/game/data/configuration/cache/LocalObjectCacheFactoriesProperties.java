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

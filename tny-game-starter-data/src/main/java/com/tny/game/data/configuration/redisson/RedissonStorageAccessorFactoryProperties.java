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

package com.tny.game.data.configuration.redisson;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage-accessor.redisson-accessor")
public class RedissonStorageAccessorFactoryProperties {

    private boolean enable = true;

    @NestedConfigurationProperty
    private RedissonStorageAccessorFactorySetting accessor = new RedissonStorageAccessorFactorySetting();

    private Map<String, RedissonStorageAccessorFactorySetting> accessors = new HashMap<>();

    public boolean isEnable() {
        return enable;
    }

    public RedissonStorageAccessorFactoryProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public RedissonStorageAccessorFactorySetting getAccessor() {
        return accessor;
    }

    public RedissonStorageAccessorFactoryProperties setAccessor(RedissonStorageAccessorFactorySetting accessor) {
        this.accessor = accessor;
        return this;
    }

    public Map<String, RedissonStorageAccessorFactorySetting> getAccessors() {
        return accessors;
    }

    public RedissonStorageAccessorFactoryProperties setAccessors(
            Map<String, RedissonStorageAccessorFactorySetting> accessors) {
        this.accessors = accessors;
        return this;
    }

}

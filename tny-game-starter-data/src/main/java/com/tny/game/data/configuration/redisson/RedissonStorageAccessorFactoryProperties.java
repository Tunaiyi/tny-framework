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

import com.tny.game.data.configuration.*;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage-accessor.redisson-accessor")
public class RedissonStorageAccessorFactoryProperties
        extends AbstractStorageAccessorFactoryProperties<RedissonStorageAccessorFactorySetting> {

    public RedissonStorageAccessorFactoryProperties() {
        super(new RedissonStorageAccessorFactorySetting());
    }

    @Override
    public RedissonStorageAccessorFactoryProperties setEnable(boolean enable) {
        super.setEnable(enable);
        return this;
    }

    @Override
    public RedissonStorageAccessorFactorySetting getAccessor() {
        return super.getAccessor();
    }

    @Override
    public RedissonStorageAccessorFactoryProperties setAccessor(RedissonStorageAccessorFactorySetting accessor) {
        super.setAccessor(accessor);
        return this;
    }

    @Override
    public RedissonStorageAccessorFactoryProperties setAccessors(Map<String, RedissonStorageAccessorFactorySetting> accessors) {
        super.setAccessors(accessors);
        return this;
    }

}

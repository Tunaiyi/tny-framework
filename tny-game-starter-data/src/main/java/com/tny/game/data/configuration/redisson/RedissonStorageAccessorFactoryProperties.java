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

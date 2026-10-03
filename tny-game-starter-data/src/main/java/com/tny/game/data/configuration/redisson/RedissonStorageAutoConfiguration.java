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
import com.tny.game.data.redisson.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedissonStorageAccessorFactory.class)
@AutoConfigureBefore(DataAutoConfiguration.class)
@Import({
        ImportRedissonStorageAccessorFactoryDefinitionRegistrar.class
})
@EnableConfigurationProperties({
        RedissonStorageAccessorFactoryProperties.class
})
@ConditionalOnProperty(value = "tny.data.enable", matchIfMissing = true)
public class RedissonStorageAutoConfiguration {

}

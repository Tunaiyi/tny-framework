/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
